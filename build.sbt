name := "phone-app"
version := "0.0.1"
organization := "ticofab.io"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.5.14",
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "0.18.0",
  "com.lightbend.akka.management" %% "akka-management" % "0.18.0",
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "0.18.0",
  "org.wvlet.airframe" %% "airframe-log" % "0.51",
  "com.typesafe.akka" %% "akka-http" % "10.1.3"
)

assemblyJarName in assembly := "phone-app.jar"

mainClass in assembly := Some("io.ticofab.phone.DeviceApp")