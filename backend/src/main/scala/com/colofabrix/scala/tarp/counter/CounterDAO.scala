package com.colofabrix.scala.tarp.counter

import cats.effect.IO
import cats.implicits.*
import doobie.ConnectionIO
import doobie.implicits.*

object CounterDAO {

  def getOrCreate(id: Int): ConnectionIO[Counter] =
    sql"""|SELECT *
          |FROM counter
          |WHERE id = ${id};"""
      .stripMargin
      .query[Counter]
      .option
      .flatMap(createIfNotFound)

  def update(counter: Counter): ConnectionIO[Unit] =
    sql"""|UPDATE counter
          |SET count = ${counter.count};"""
      .stripMargin
      .update
      .run
      .void

  private def createIfNotFound(counterFound: Option[Counter]): ConnectionIO[Counter] = counterFound match {
    case Some(counter) =>
      counter.pure[ConnectionIO]
    case None =>
      sql"""|INSERT INTO counter (count)
            |VALUES (0);""".stripMargin
        .update
        .withUniqueGeneratedKeys[Long]("id")
        .flatMap { id =>
          sql"""|SELECT *
                |FROM counter
                |WHERE id = $id;"""
            .stripMargin
            .query[Counter]
            .unique
        }
  }

}
