package io.ticofab.phone.phone

import akka.actor.{Actor, ActorRef}
import io.ticofab.phone.phone.Phone.{CheckMatchingWith, YouMatchedWith}
import wvlet.log.LogSupport

import scala.math.{pow, sqrt}

class Phone(myLat: Int, myLon: Int) extends Actor with LogSupport {

  info(s"phone actor ${self.path.name} created for location ($myLat, $myLon)")

  override def receive = {
    case CheckMatchingWith(phone, lat, lon) =>
      if (isCloseEnough(lat, lon)) {
        logMatched(self, phone)
        phone ! YouMatchedWith(self)
      }

    case YouMatchedWith(phone) =>
      logMatched(self, phone)
  }

  // logs that this phone matched with another phone
  def logMatched(me: ActorRef, it: ActorRef) = info(s"phone ${me.path.name}, matched with phone ${it.path.name}")

  // checks if these coordinates are close enough
  def isCloseEnough(lat: Int, lon: Int) = sqrt(pow(myLat - lat, 2) + pow(myLon - lon, 2)) < 1.42
}

object Phone {

  case class CheckMatchingWith(phone: ActorRef, lat: Int, lon: Int)

  case class YouMatchedWith(phone: ActorRef)

}
