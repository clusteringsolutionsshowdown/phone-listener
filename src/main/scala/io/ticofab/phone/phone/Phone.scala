package io.ticofab.phone.phone

import akka.actor.{Actor, ActorRef}
import io.ticofab.phone.phone.Phone.{CheckMatching, Matched}
import wvlet.log.LogSupport

import scala.math.{pow, sqrt}

class Phone(myLat: Int, myLon: Int) extends Actor with LogSupport {

  info(s"phone actor ${self.path.name} created for location ($myLat, $myLon)")

  override def receive = {
    case CheckMatching(phone, lat, lon) =>
      val distance = sqrt(pow(myLat - lat, 2) + pow(myLon - lon, 2))
      if (distance < 1.42) {
        logMatchedMsg(self.path.name, phone.path.name)
        phone ! Matched(self)
      }

    case Matched(phone) =>
      logMatchedMsg(self.path.name, phone.path.name)
  }

  val logMatchedMsg = (me: String, other: String) => info(s"phone $me, matched with phone $other")
}

object Phone {

  case class CheckMatching(phone: ActorRef, lat: Int, lon: Int)

  case class Matched(phone: ActorRef)

}
