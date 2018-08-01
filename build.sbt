name := "phone-app"
version := "0.0.1"
organization := "ticofab.io"
scalaVersion := "2.12.6"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-http" % "10.1.3",
    "com.typesafe.akka" %% "akka-stream" % "2.5.14",
    "org.wvlet.airframe" %% "airframe-log" % "0.51"
  )
}
