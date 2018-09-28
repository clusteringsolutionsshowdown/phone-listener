package io.ticofab.phonecommon

import scala.math.{pow, sqrt}

case class Location(lat: Int, lon: Int) {
  override def toString = s"($lat, $lon)"

  def isCloseEnoughTo(location: Location) = sqrt(pow(location.lat - lat, 2) + pow(location.lon - lon, 2)) < 1.42
}
