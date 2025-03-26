package com.colofabrix.scala.tarp.counter

import cats.effect.IO
import cats.implicits.*
import org.http4s.HttpRoutes
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

object CounterHttp:

  private val getCounterEndpoint =
    endpoint
      .summary("Get counter")
      .get
      .in("api" / "counter")
      .out(jsonBody[Long])

  private val getCounterRoute =
    Http4sServerInterpreter[IO]()
      .toRoutes(getCounterEndpoint.serverLogicSuccess(_ => CounterService.getOrCreate))

  private val addOneEndpoint =
    endpoint
      .summary("Add one to counter")
      .post
      .in("api" / "counter" / "add-one")
      .out(jsonBody[Long])

  private val addOneRoute =
    Http4sServerInterpreter[IO]().toRoutes(addOneEndpoint.serverLogicSuccess(_ => CounterService.addOne))

  def endpoints: List[AnyEndpoint] =
    List(getCounterEndpoint, addOneEndpoint)

  def routes: HttpRoutes[IO] =
    getCounterRoute <+> addOneRoute
