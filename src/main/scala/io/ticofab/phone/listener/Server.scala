package io.ticofab.phone.listener

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import io.ticofab.phonecommon.Location
import io.ticofab.phonecommon.Messages.DeviceConnected
import wvlet.log.LogSupport

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class Server extends Actor with LogSupport {
  type HandlingFlow = Flow[Message, Message, _]

  override def receive = Actor.emptyBehavior

  implicit val as = context.system

  // http server to control the rate per second of inputs
  implicit val am = ActorMaterializer()
  val routes = path("connect") {
    // curl http://0.0.0.0:8080/connect?lat=1&lon=2
    parameters("lat".as[Int], "lon".as[Int]) { (lat, lon) =>
      info(s"phone connected at location ($lat, $lon)")
      val handlingFlow = (context.parent ? DeviceConnected(Location(lat, lon))) (3.seconds).mapTo[HandlingFlow]
      onComplete(handlingFlow) {
        case Success(flow) => handleWebSocketMessages(flow)
        case Failure(err) => complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  } ~ get {
    // curl http://0.0.0.0:8080
    complete("Phone App Listener is alive!\n")
  }

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
