import com.github.nscala_time.time.Imports._

val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r

lazy val root = (project in file(".")).
  enablePlugins(DockerPlugin, GitVersioning, GitBranchPrompt).
  settings(packAutoSettings ++ useJGit).
  settings(
    name := "akka-cluster-sample101",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("–encoding", "UTF-8", "–deprecation", "on", "-feature", "-language:postfixOps"),
    packGenerateWindowsBatFile := false,
    packJarNameConvention := "original",
    libraryDependencies := Seq(
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.7",
      "com.typesafe.akka" %% "akka-cluster" % "2.4.7",
      "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.7",
      "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.7",
      "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
      "com.typesafe.akka" %% "akka-http-experimental" % "2.4.7",
      "com.typesafe.akka" %% "akka-http-xml-experimental" % "2.4.7",
      "com.typesafe.akka" %% "akka-persistence" % "2.4.7",
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.17",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
      "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1",
      "ch.qos.logback" %  "logback-classic" % "1.1.7"

    ),
    mainClass in docker := Some("sample.cluster.simple.SimpleClusterApp"),
    git.baseVersion := "0.1.3",
    git.useGitDescribe := true,
    dockerfile in docker := {
      val jarFile: File = sbt.Keys.`package`.in(Compile).value
      val classpath = (managedClasspath in Compile).value
      val mainclass = mainClass.in(docker).value.getOrElse("")
      val classpathString = classpath.files.map("/app/libs/" + _.getName).mkString(":") + ":" + s"/app/${jarFile.getName}"
      val `modify@` = (format: String, file: File) => new DateTime(file.lastModified()).toString(format)

        new Dockerfile {
        from("java:8-jre-alpine")
        classpath.files.groupBy(`modify@`("MM/dd/yyyy",_)).map { case (g, files) =>
          add(files, "/app/libs/")
        }
        //add(classpath.files, "/app/libs/")
        add(jarFile, "/app/")
        env("JAVA_OPTS", "")
        entryPoint("java","${JAVA_OPTS}", "-cp", classpathString, mainclass)
      }
    },
    imageNames in docker := Seq(
      ImageName(
        namespace = Some("index.tenxcloud.com/henryrao"),
        repository = "akka-cluster",
        tag = Some(version.value)
      )
    )
  )

