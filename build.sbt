name := "pl-class02"

version := "1.0"

scalaVersion := "2.12.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

parallelExecution in Test := false
