package com.colofabrix.scala.tarp.counter

import cats.effect.IO
import cats.implicits.*
import com.colofabrix.scala.tarp.DbSetup
import doobie.ConnectionIO

object CounterService:

  def getOrCreate: IO[Long] =
    DbSetup.run {
      CounterDAO
        .getOrCreate(1)
        .map(_.count)
    }

  def addOne: IO[Long] =
    DbSetup.run {
      for {
        counter       <- CounterDAO.getOrCreate(1)
        counterUpdated = counter.addOne
        _             <- CounterDAO.update(counterUpdated)
      } yield counterUpdated.count
    }
