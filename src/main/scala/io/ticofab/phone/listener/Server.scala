package io.ticofab.phone.listener

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.stream.ActorMaterializer
import io.ticofab.phonecommon.Location
import io.ticofab.phonecommon.Messages.PhoneConnected
import wvlet.log.LogSupport

class Server extends Actor with LogSupport {
  override def receive = Actor.emptyBehavior

  implicit val as = context.system

  // http server to control the rate per second of inputs
  implicit val am = ActorMaterializer()
  val routes = path("connect") {
    // curl http://0.0.0.0:8080/connect?lat=1&lon=2
    parameters("lat".as[Int], "lon".as[Int]) { (lat, lon) =>
      info(s"phone connected at location ($lat, $lon)")
      context.parent ! PhoneConnected(Location(lat, lon))
      complete("Connection request received\n")
    }
  } ~ get {
    // curl http://0.0.0.0:8080
    complete("Phone App Listener is alive!\n")
  }

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
