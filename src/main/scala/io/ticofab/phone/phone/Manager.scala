package io.ticofab.phone.phone

import akka.actor.{Actor, Props}
import io.ticofab.phone.phone.Manager.PhoneConnected
import io.ticofab.phone.phone.Phone.CheckMatching
import wvlet.log.LogSupport

class Manager extends Actor with LogSupport {
  info("starting")

  override def receive = {
    case PhoneConnected(lat, lon) =>
      val currentChildren = context.children

      // spawn a new actor for each phone
      val phone = context.actorOf(Props(new Phone(lat, lon)), s"${currentChildren.size + 1}")
      currentChildren.foreach(_ ! CheckMatching(phone, lat, lon))
  }
}

object Manager {

  case class PhoneConnected(lat: Int, lon: Int)

}
