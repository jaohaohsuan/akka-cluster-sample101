//import sbt.Keys.{artifactPath}
//
//name := "akka-cluster-sample101"
//
//version := "1.0"
//

//
//libraryDependencies := Seq(
//  "com.typesafe.akka" %% "akka-cluster" % "2.4.7",
//  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.7"
//)
//
//enablePlugins(DockerPlugin)
//
//packAutoSettings
//
//dockerfile in docker := {
//  new Dockerfile {
//
//    from("java:8-jdk-alpine")
//  }
//}

lazy val root = (project in file(".")).
  enablePlugins(DockerPlugin).
  settings(packAutoSettings).
  settings(
    name := "akka-cluster-sample101",
    scalaVersion := "2.11.8",
    packGenerateWindowsBatFile := false,
    packJarNameConvention := "original",
    libraryDependencies := Seq(
      "com.typesafe.akka" %% "akka-cluster" % "2.4.7",
      "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.7",
      "io.fabric8.forge" % "kubernetes" % "2.2.211"
    ),
    dockerfile in docker := {
      val jarFile: File = sbt.Keys.`package`.in(Compile).value
      val classpath = (managedClasspath in Compile).value
      val mainclass = mainClass.in(Compile).value.getOrElse("")
      val classpathString = classpath.files.map("/app/libs/" + _.getName).mkString(":") + ":" + s"/app/${jarFile.getName}"

      val sdf = new java.text.SimpleDateFormat("MM/dd/yyyy")


        new Dockerfile {
        from("java:8-jre-alpine")
        classpath.files.groupBy{ file => sdf.format(file.lastModified())}.foreach { g =>
          add(g._2, "/app/libs/")
        }
        //add(classpath.files, "/app/libs/")
        add(jarFile, "/app/")
        entryPoint("java", "-cp", classpathString, mainclass)
      }
    }
  )
