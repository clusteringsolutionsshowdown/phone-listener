package io.ticofab.phone

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import io.ticofab.phone.listener.Server
import io.ticofab.phonecommon.Messages.{CheckMatchingWith, PhoneActorReady, PhoneConnected, RegisterNode}
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

object PhoneApp extends App with LogSupport {
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)
  info("phone app starting")
  val as = ActorSystem("showdown")
  as.actorOf(Props[Supervisor], "supervisor")
}

class Supervisor extends Actor with LogSupport {
  info(s"starting, $self")

  // my state: the connected nodes
  var nodes: Map[ActorRef, Int] = Map()

  def receive = {
    case RegisterNode =>
      nodes = nodes + (sender -> 0)

    case pc: PhoneConnected =>
      // forward it to node with minimum number of connected phones
      val chosenNode = nodes.minBy(_._2)._1
      chosenNode ! pc

    case PhoneActorReady(phone, itsLocation) =>
      debug(s"phone actor is ready for location $itsLocation: $phone")
      val updatedLoad = nodes.getOrElse(sender, 0) + 1
      nodes = nodes + (sender -> updatedLoad)

      // TODO: perform load check and maybe trigger scaling or downscaling

      nodes.foreach { case (node, _) => node ! CheckMatchingWith(phone, itsLocation) }

  }

  // start actors
  context.actorOf(Props(new Server))
}
