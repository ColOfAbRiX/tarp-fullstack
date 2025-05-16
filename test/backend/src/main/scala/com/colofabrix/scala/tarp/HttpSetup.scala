package com.colofabrix.scala.tarp

import cats.effect.IO
import cats.implicits.*
import com.colofabrix.scala.tarp.counter.*
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{ Logger => LoggerMiddleware }
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax.LoggerInterpolator
import sttp.tapir.emptyInput
import sttp.tapir.files.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object HttpSetup:

  implicit private val logger: Logger[IO] =
    Slf4jLogger.getLogger()

  def setup(): IO[Unit] =
    val crossOriginResourceSharingPolicy =
      CORS
        .policy
        .withAllowOriginHostCi(_ => com.colofabrix.scala.tarp.TarpConfig.config.devMode)

    val httpApp =
      crossOriginResourceSharingPolicy(allRoutes)

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(com.colofabrix.scala.tarp.TarpConfig.config.http.port)
      .withHttpApp(httpApp.orNotFound)
      .build
      .use(_ => IO.never)
      .start
      .void

  private val frontendServerLogic =
    staticFilesGetServerEndpoint[IO](emptyInput)(
      "../frontend/dist",
      FilesOptions.default.defaultFile(List("index.html")),
    )

  private val frontendEndpoint =
    frontendServerLogic
      .endpoint
      .summary("Frontend served from '../frontend/dist' on the file system")

  private val frontendRoutes =
    Http4sServerInterpreter[IO]().toRoutes(frontendServerLogic)

  private val docsEntpoint =
    SwaggerInterpreter().fromEndpoints[IO](
      CounterHttp.endpoints :+ frontendEndpoint,
      "Backend - TARP Stack",
      "1.0",
    )

  private val allRoutes =
    val loggerMiddleware =
      LoggerMiddleware
        .httpRoutes(
          logHeaders = true,
          logBody = true,
          redactHeadersWhen = _ => !com.colofabrix.scala.tarp.TarpConfig.config.devMode,
          logAction = Some((msg: String) => info"$msg"),
        )
        .apply(_)

    Http4sServerInterpreter[IO]().toRoutes(docsEntpoint) <+>
    loggerMiddleware(CounterHttp.routes) <+>
    frontendRoutes
