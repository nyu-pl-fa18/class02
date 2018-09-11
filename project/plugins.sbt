logLevel := Level.Debug

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")
