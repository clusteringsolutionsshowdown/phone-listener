name := "phone-app"
version := "0.0.1"
organization := "ticofab.io"
scalaVersion := "2.12.6"

lazy val phoneCommon = RootProject(file("../phone-common"))
val main = Project(id = "phone-app", base = file(".")).dependsOn(phoneCommon)
