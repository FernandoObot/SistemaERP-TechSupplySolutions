ThisBuild / scalaVersion := "3.9.0"
ThisBuild / organization := "com.techsupply"
ThisBuild / version      := "0.1.0-SNAPSHOT"

val catsVersion             = "2.13.0"
val catsEffectVersion       = "3.6.4"
val http4sVersion           = "0.23.38"
val tapirVersion            = "1.14.1"
val circeVersion            = "0.14.17"
val munitVersion            = "1.3.6"
val munitCatsEffectVersion  = "2.2.1"
val logbackVersion          = "1.6.6"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
  Test / fork := true
)

lazy val domain = (project in file("domain"))
  .settings(commonSettings)
  .settings(
    name := "erp-domain",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion
    )
  )

lazy val application = (project in file("application"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(
    name := "erp-application",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "munit-cats-effect" % munitCatsEffectVersion % Test,
      "org.scalameta" %% "munit" % munitVersion % Test
    )
  )

lazy val adapters = (project in file("adapters"))
  .dependsOn(application)
  .settings(commonSettings)
  .settings(
    name := "erp-adapters",
    libraryDependencies ++= Seq(
      "org.typelevel"     %% "cats-effect"          % catsEffectVersion,
      "org.http4s"        %% "http4s-ember-server"  % http4sVersion,
      "org.http4s"        %% "http4s-dsl"           % http4sVersion,
      "org.http4s"        %% "http4s-circe"         % http4sVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "io.circe"          %% "circe-core"           % circeVersion,
      "io.circe"          %% "circe-generic"        % circeVersion,
      "org.tpolecat"      %% "skunk-core"           % "1.0.0",
      "org.typelevel"     %% "otel4s-core"          % "0.16.0",
      "org.scalameta"     %% "munit"                % munitVersion            % Test,
      "org.typelevel"     %% "munit-cats-effect"    % munitCatsEffectVersion  % Test
    )
  )

lazy val infrastructure = (project in file("infrastructure"))
  .dependsOn(application)
  .settings(commonSettings)
  .settings(
    name := "erp-infrastructure",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion
    )
  )

lazy val server = (project in file("server"))
  .dependsOn(adapters, infrastructure)
  .settings(commonSettings)
  .settings(
    name := "erp-server",
    Compile / run / fork := true,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"       % catsEffectVersion,
      "ch.qos.logback" % "logback-classic"   % logbackVersion
    )
  )

lazy val root = (project in file("."))
  .aggregate(domain, application, adapters, infrastructure, server)
  .settings(
    name := "erp-backend",
    publish / skip := true
  )
