val buildVersion = sys.props.get("ROOT_BUILD_NUMBER").orElse(sys.env.get("ROOT_BUILD_NUMBER")).getOrElse("SNAPSHOT")

val commonSettings = Seq(
  autoAPIMappings := true,
  crossScalaVersions := ProjectSettings.version.scalaCrossBuild,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
  organization := "com.solarmosaic.client." + ProjectSettings.projectRootName,
  publishArtifact := true,
  publishArtifact in Test := false,
  publishTo := Some("MosaicArtifactory" at "http://art.intranet.solarmosaic.com/artifactory/internal"),
  resolvers := Seq("Artifactory" at "http://art.intranet.solarmosaic.com/artifactory/repo"),
  sbtVersion := "0.13.9",
  scalaVersion := ProjectSettings.version.scala211,
  // Don't download javadocs from transitive dependencies
  transitiveClassifiers := Seq(Artifact.SourceClassifier),
  version := "0.1." + buildVersion
)

// Let integration tests inherit test configs and classpaths
lazy val it = config("it") extend Test
lazy val itSettings = inConfig(it)(Defaults.testSettings)

lazy val client = (project in file("."))
  .configs(it)
  .settings(commonSettings: _*)
  .settings(itSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      // Compile time dependencies
      "javax.mail" % "mail" % "1.4.7",

      // Test dependencies
      "org.jvnet.mock-javamail" % "mock-javamail" % "1.9" % "test",
      "org.specs2" %% "specs2-core" % ProjectSettings.version.specs2 % "it, test",
      "org.specs2" %% "specs2-mock" % ProjectSettings.version.specs2 % "it, test"
    ),
    name := ProjectSettings.projectRootName
  )
