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

import com.github.nscala_time.time.Imports._

lazy val root = (project in file(".")).
  enablePlugins(DockerPlugin, GitVersioning).
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
    git.baseVersion := "0.1.2",
    dockerfile in docker := {
      val jarFile: File = sbt.Keys.`package`.in(Compile).value
      val classpath = (managedClasspath in Compile).value
      val mainclass = mainClass.in(Compile).value.getOrElse("")
      val classpathString = classpath.files.map("/app/libs/" + _.getName).mkString(":") + ":" + s"/app/${jarFile.getName}"
      val `modify@` = (format: String, file: File) => new DateTime(file.lastModified()).toString(format)

        new Dockerfile {
        from("java:8-jre-alpine")
        classpath.files.groupBy(`modify@`("MM/dd/yyyy",_)).map { case (g, files) =>
          add(files, "/app/libs/")
        }
        //add(classpath.files, "/app/libs/")
        add(jarFile, "/app/")
        entryPoint("java", "-cp", classpathString, mainclass)
      }
    }
  )

useJGit

