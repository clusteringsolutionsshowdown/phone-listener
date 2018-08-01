package io.ticofab.phone

import akka.actor.{Actor, ActorSystem, Props}
import io.ticofab.phone.listener.Server
import io.ticofab.phone.phone.Manager

object PhoneApp extends App {

  class Supervisor extends Actor {
    override def receive = Actor.emptyBehavior

    val manager = context.actorOf(Props[Manager])
    val server = context.actorOf(Props(new Server(manager)))
  }

  val as = ActorSystem("phoneapp")
  as.actorOf(Props[Supervisor])
}
