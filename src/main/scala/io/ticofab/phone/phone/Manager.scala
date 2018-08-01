package io.ticofab.phone.phone

import akka.actor.{Actor, Props}
import io.ticofab.phone.phone.Manager.PhoneConnected

class Manager extends Actor {
  override def receive = {
    case PhoneConnected(lat, lon) =>
      // spawn a new actor for each phone
      context.actorOf(Props(new Phone(lat, lon)))
  }
}

object Manager {

  case class PhoneConnected(lat: Int, lon: Int)

}
