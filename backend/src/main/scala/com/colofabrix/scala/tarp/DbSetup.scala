package com.colofabrix.scala.tarp

import cats.effect.IO
import cats.effect.Resource
import com.colofabrix.scala.tarp.TarpConfig.config
import doobie.ConnectionIO
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import org.flywaydb.core.api.output.MigrateResult
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object DbSetup:

  implicit private val logger: Logger[IO] =
    Slf4jLogger.getLogger()

  def setup(): IO[Unit] =
    for
      _ <- Logger[IO].trace(s"Database URL: $dbUrl")
      _ <- raiseUnlessDbUp()
      _ <- migrate()
      _ <- Logger[IO].info("Database system initialized.")
    yield ()

  def run[A](sqls: ConnectionIO[A]): IO[A] =
    transactor.use(sqls.transact[IO])

  private val dbUrl: String =
    s"jdbc:postgresql://${config.db.host}:${config.db.port}/${config.db.database}?currentSchema=${config.db.schema}"

  private def raiseUnlessDbUp(): IO[Unit] =
    for
      isUp   <- run(sql"SELECT 1;".query[Long].unique.map(_ == 1L))
      result <- IO.raiseUnless(isUp)(new RuntimeException(s"Postgres ${config.db.database} database is unreachable."))
    yield result

  private def migrate(): IO[MigrateResult] =
    IO.blocking {
      Flyway
        .configure
        .dataSource(dbUrl, config.db.user, config.db.password)
        .group(true)
        .outOfOrder(false)
        .table(config.db.migrationsTable)
        .locations(config.db.migrationsLocations*)
        // .failOnMissingLocations(true)
        .load
        .migrate
    }

  private val transactor: Resource[IO, HikariTransactor[IO]] =
    for
      ec  <- ExecutionContexts.fixedThreadPool[IO](32)
      res <- HikariTransactor.newHikariTransactor[IO](config.db.driver, dbUrl, config.db.user, config.db.password, ec)
    yield res
