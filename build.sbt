name := "eshomo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.27",	
  "commons-codec" % "commons-codec" % "1.8"
)     

play.Project.playJavaSettings
