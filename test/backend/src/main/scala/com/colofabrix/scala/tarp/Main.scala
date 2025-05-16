package com.colofabrix.scala.tarp

import cats.effect.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

/**
 * Application entry point
 */
object Main extends IOApp:

  implicit private val logger: Logger[IO] =
    Slf4jLogger.getLogger()

  def run(args: List[String]): IO[ExitCode] =
    for
      _      <- DbSetup.setup()
      _      <- HttpSetup.setup()
      result <- IO.never.as(ExitCode.Success)
    yield result
