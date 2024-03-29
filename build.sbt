name := "eshomo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.27",	
  "commons-codec" % "commons-codec" % "1.8",
  "commons-io" % "commons-io" % "2.4",
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.0.4" % "test"
)     

javacOptions ++= Seq("-Xlint:deprecation","-Xlint:unchecked") 


play.Project.playJavaSettings
