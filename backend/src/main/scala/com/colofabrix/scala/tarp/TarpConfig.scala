package com.colofabrix.scala.tarp

import pureconfig.*
import com.comcast.ip4s.{ Ipv4Address, Port }
import io.github.arainko.ducktape.*
import pureconfig.error.CannotConvert

object TarpConfig:

  val config: TarpConfig =
    ConfigSource
      .default
      .at("tarp")
      .loadOrThrow[TarpConfig]

final case class TarpConfig(
  devMode: Boolean,
  http: TarpHttpConfig,
  db: TarpDbConfig,
) derives ConfigReader

final case class TarpHttpConfig(
  port: Port,
  bind: Ipv4Address,
) derives ConfigReader

final case class TarpDbConfig(
  driver: String,
  host: String,
  port: Int,
  schema: String,
  database: String,
  user: String,
  password: String,
  migrationsTable: String,
  migrationsLocations: List[String],
) derives ConfigReader

given portConfigReader: ConfigReader[Port] =
  ConfigReader[Int].emap { intPort =>
    Port.fromInt(intPort) match
      case Some(port) => Right(port)
      case None       => Left(CannotConvert(intPort.toString, "Port", "Unprocessable port number"))
  }

given ipv4AddressConfigReader: ConfigReader[Ipv4Address] =
  ConfigReader[String].emap { stringIp =>
    Ipv4Address.fromString(stringIp) match
      case Some(ip) => Right(ip)
      case None       => Left(CannotConvert(stringIp, "Ipv4Address", "Unprocessable IPv4"))
  }
