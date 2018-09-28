package io.ticofab.phonecommon

import akka.actor.ActorRef
import akka.stream.SourceRef

object Messages {

  case object RegisterNode

  case class DeviceConnected(location: Location)

  case class DeviceActorReady(manager: ActorRef, deviceActor: ActorRef, location: Location, sourceRef: SourceRef[String])

  case class CheckMatchingWith(phone: ActorRef, location: Location)

  case class YouMatchedWith(device: ActorRef)

  case class MessageForMatchedDevice(msg: String)

}
