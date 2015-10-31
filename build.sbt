val buildVersion = sys.props.get("ROOT_BUILD_NUMBER")
  .orElse(sys.env.get("ROOT_BUILD_NUMBER"))
  .map("." + _).getOrElse("-SNAPSHOT")

val commonSettings = Seq(
  autoAPIMappings := true,
  crossScalaVersions := ProjectSettings.version.scalaCrossBuild,
  description := "Scala mailer built on top of the JavaMail API.",
  developers := List(
    Developer(
      "kflorence",
      "Kyle Florence",
      "kyle.florence@joinmosaic.com",
      url("https://github.com/kflorence")
    )
  ),
  homepage := Some(url("https://github.com/solarmosaic/mail-client")),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  organization := "com.solarmosaic.client." + ProjectSettings.projectRootName,
  organizationHomepage := Some(url("https://github.com/solarmosaic")),
  packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
    new java.util.jar.Attributes.Name("BuildDate") -> new java.util.Date().toString
  ),
  pomIncludeRepository := { _ => false },
  publishArtifact := true,
  publishArtifact in Test := false,
  publishMavenStyle := true,
  publishTo := {
    Some("MosaicArtifactory" at "http://art.intranet.solarmosaic.com/artifactory/internal")
    //      val nexus = "https://oss.sonatype.org/"
    //      if (isSnapshot.value)
    //        Some("snapshots" at nexus + "content/repositories/snapshots")
    //      else
    //        Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  resolvers := Seq("Artifactory" at "http://art.intranet.solarmosaic.com/artifactory/repo"),
  //resolvers := Seq("scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"),
  sbtVersion := "0.13.9",
  scalaVersion := ProjectSettings.version.scala211,
  scmInfo := Some(ScmInfo(
    url("https://github.com/solarmosaic/mail-client"),
    "https://github.com/solarmosaic/mail-client.git"
  )),
  startYear := Some(2015),
  // Don't download javadocs from transitive dependencies
  transitiveClassifiers := Seq(Artifact.SourceClassifier),
  version := "0.1" + buildVersion
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
