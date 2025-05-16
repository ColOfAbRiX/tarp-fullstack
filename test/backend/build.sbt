import org.typelevel.scalacoptions.ScalacOptions

val scala3Version = "3.6.2"

Global / run / fork           := true
Global / onChangedBuildSource := ReloadOnSourceChanges
Global / tpolecatExcludeOptions ++=
  Set(
    ScalacOptions.warnUnusedImports,
    ScalacOptions.warnUnusedPrivates,
    ScalacOptions.warnUnusedLocals,
  )

Compile / compile / wartremoverErrors ++= Warts.unsafe
Compile / compile / wartremoverErrors -= Wart.Any // The code keeps failing on string interpolations

lazy val root =
  project
    .in(file("."))
    .settings(
      name              := "tarp",
      organization      := "com.colofabrix.scala",
      version           := "0.1.0-SNAPSHOT",
      scalaVersion      := scala3Version,
      scalaVersion      := scala3Version,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      libraryDependencies ++= List(
        // Cats
        "org.typelevel" %% "cats-effect" % "3.5.4",

        // Configuration
        "com.github.pureconfig" %% "pureconfig-core" % "0.17.8",

        // Log4j2
        "org.typelevel" %% "log4cats-core"   % "2.7.0",
        "ch.qos.logback" % "logback-classic" % "1.2.10",

        // Ducktape
        "io.github.arainko" %% "ducktape" % "0.2.6",

        // Monocle
        "dev.optics" %% "monocle-core"  % "3.2.0",
        "dev.optics" %% "monocle-macro" % "3.2.0",

        // Tapir
        "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % "1.11.10",
        "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "1.11.10",
        "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.11.10",

        // Http4s
        "org.http4s" %% "http4s-ember-server" % "0.23.30",
        "org.http4s" %% "http4s-circe"        % "0.23.30",
        "org.http4s" %% "http4s-dsl"          % "0.23.30",
        "org.http4s" %% "http4s-ember-client" % "0.23.30",

        // Circe
        "io.circe" %% "circe-parser"  % "0.14.6",
        "io.circe" %% "circe-generic" % "0.14.6",
        "io.circe" %% "circe-literal" % "0.14.6",

        // Doobie
        "org.tpolecat" %% "doobie-core"     % "1.0.0-RC5",
        "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC5",
        "org.tpolecat" %% "doobie-hikari"   % "1.0.0-RC5",

        // Flyway
        "org.flywaydb" % "flyway-core"                % "10.11.1",
        "org.flywaydb" % "flyway-database-postgresql" % "10.11.1" % "runtime",

        // Test dependencies.
        "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      ),
    )
