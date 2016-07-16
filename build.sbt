import com.github.nscala_time.time.Imports._

val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r

lazy val root = (project in file(".")).
  enablePlugins(DockerPlugin, GitVersioning, GitBranchPrompt).
  settings(packAutoSettings ++ useJGit).
  settings(
    name := "akka-cluster-sample101",
    scalaVersion := "2.11.8",
    packGenerateWindowsBatFile := false,
    packJarNameConvention := "original",
    libraryDependencies := Seq(
      "com.typesafe.akka" %% "akka-cluster" % "2.4.7",
      "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.7",
      "com.typesafe.akka" %% "akka-persistence" % "2.4.7",
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.17",
      "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1",
      "io.fabric8.forge" % "kubernetes" % "2.2.211"
    ),
    git.baseVersion := "0.1.3",
    git.useGitDescribe := true,
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
    },
    imageNames in docker := Seq(
      ImageName(
        namespace = Some("index.tenxcloud.com/henryrao"),
        repository = "akka-cluster",
        tag = Some(version.value)
      )
    )
  )

