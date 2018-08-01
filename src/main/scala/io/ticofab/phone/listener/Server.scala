package io.ticofab.phone.listener

import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.stream.ActorMaterializer
import io.ticofab.phone.phone.Manager.PhoneConnected
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext.Implicits.global

class Server(phoneManager: ActorRef) extends Actor with LogSupport {
  override def receive = Actor.emptyBehavior

  implicit val as = context.system

  // http server to control the rate per second of inputs
  implicit val am = ActorMaterializer()
  val routes = path("connect") {
    parameters("lat".as[Int], "lon".as[Int]) { (lat, lon) =>
      info(s"phone connected at location ($lat, $lon)")
      phoneManager ! PhoneConnected(lat, lon)
      complete("Connection request received")
    }
  } ~ get {
    // curl http://0.0.0.0:8080     --> simple health check
    complete("Phone App Listener is alive!\n")
  }

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
