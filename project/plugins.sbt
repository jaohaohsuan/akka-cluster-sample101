logLevel := Level.Error
libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.12.0"
)
addSbtPlugin("se.marcuslonnberg" % "sbt-docker"    % "1.4.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-pack"      % "0.8.0")
addSbtPlugin("io.spray"          % "sbt-revolver"  % "0.8.0")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo" % "0.6.1")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"       % "0.8.5")
addSbtPlugin("com.github.gseitz" % "sbt-release"   % "1.0.3")