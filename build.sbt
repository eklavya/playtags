import SonatypeKeys._

// Import default settings. This changes `publishTo` settings to use the Sonatype repository and add several commands for publishing.
xerial.sbt.Sonatype.sonatypeSettings

name := """playtags"""

version := "1.1"

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.10.4", scalaVersion.value)

libraryDependencies ++= Seq(
  "com.scalatags" %% "scalatags" % "0.4.2",
  "com.typesafe.play" %% "play" % "2.3.7"
)

profileName := "com.github.eklavya"

organization := "com.github.eklavya"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/eklavya/playtags</url>
  <licenses>
    <license>
      <name>Apache 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:eklavya/playtags.git</url>
    <connection>scm:git:git@github.com:eklavya/playtags.git</connection>
  </scm>
  <developers>
    <developer>
      <id>saurabh.rawat90@gmail.com</id>
      <name>Saurabh Rawat</name>
    </developer>
  </developers>
)
