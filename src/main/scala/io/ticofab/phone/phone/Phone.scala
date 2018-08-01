package io.ticofab.phone.phone

import akka.actor.{Actor, ActorRef}

import math.{pow, sqrt}
import io.ticofab.phone.phone.Phone.{CheckMatching, Matched}
import wvlet.log.LogSupport

class Phone(myLat: Int, myLon: Int) extends Actor with LogSupport {
  override def receive = {
    case CheckMatching(phone, lat, lon) =>
      val distance = sqrt(pow(myLat - lat, 2) + pow(myLon - lon, 2))
      if (distance < 1.42) {
        info(s"matched with phone ${phone.path.name}")
        phone ! Matched(self)
      }

    case Matched(phone) =>
      info(s"matched with phone ${phone.path.name}")
  }
}

object Phone {

  case class CheckMatching(phone: ActorRef, lat: Int, lon: Int)

  case class Matched(phone: ActorRef)

}
