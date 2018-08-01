package io.ticofab.phone

import akka.actor.{Actor, ActorSystem, Props}
import io.ticofab.phone.listener.Server
import io.ticofab.phone.phone.Manager
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogFormatter, LogSupport, Logger}

object PhoneApp extends App with LogSupport {

  class Supervisor extends Actor with LogSupport {
    info("starting")

    override def receive = Actor.emptyBehavior

    val manager = context.actorOf(Props[Manager])
    val server = context.actorOf(Props(new Server(manager)))
  }

  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  info("phone app starting")
  val as = ActorSystem("phoneapp")
  as.actorOf(Props[Supervisor])
}
