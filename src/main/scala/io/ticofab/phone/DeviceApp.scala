package io.ticofab.phone

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.management.AkkaManagement
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.pattern.{ask, pipe}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Sink}
import akka.stream.{ActorMaterializer, FlowShape}
import io.ticofab.phone.listener.Server
import io.ticofab.phonecommon.Messages._
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object DeviceApp extends App with LogSupport {
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)
  info("phone app starting")
  val as = ActorSystem("showdown")

  AkkaManagement(as).start()
  ClusterBootstrap(as).start()

  as.actorOf(Props[Supervisor], "supervisor")
}

class Supervisor extends Actor with LogSupport {
  implicit val as = context.system
  implicit val am = ActorMaterializer()

  info(s"starting, $self")

  // my state: the connected nodes
  var nodes: Map[ActorRef, Int] = Map()

  def receive = {
    case RegisterNode =>
      debug(s"node joined: $sender")
      nodes = nodes + (sender -> 0)

    case dc: DeviceConnected =>
      // forward it to node with minimum number of connected phones
      val chosenNode = {
        val nodesForLog = nodes.map { case (k, v) => (k.path.name, v) }
        val nodeForDevice = nodes.minBy(_._2)._1
        debug(s"who to send this device to? loads are $nodesForLog. Sending it to ${nodeForDevice.path.name}")
        nodeForDevice
      }

      (chosenNode ? dc) (3.seconds)
        .mapTo[DeviceActorReady]
        .map { case DeviceActorReady(manager, deviceActor, itsLocation, sourceRef) =>

          // some side effecting stuff
          debug(s"device actor is ready for location $itsLocation: $deviceActor")
          val updatedLoad = nodes.getOrElse(manager, 0) + 1
          nodes = nodes + (manager -> updatedLoad)


          // TODO: perform load check and maybe trigger scaling or downscaling

          nodes.foreach { case (node, _) => node ! CheckMatchingWith(deviceActor, itsLocation) }

          // create and send flow back
          Flow.fromGraph(GraphDSL.create() { implicit b =>

            val textMsgFlow = b.add(Flow[Message]
              .mapAsync(1) {
                case tm: TextMessage => tm.toStrict(3.seconds).map(_.text)
                case _ => Future.failed(new Exception("yuck"))
              })

            val pubSrc = b.add(sourceRef.map(TextMessage(_)))

            textMsgFlow ~> Sink.foreach[String](deviceActor ! MessageForMatchedDevice(_))
            FlowShape(textMsgFlow.in, pubSrc.out)
          })
        }
        .pipeTo(sender)
  }

  // start actors
  context.actorOf(Props(new Server))
}
