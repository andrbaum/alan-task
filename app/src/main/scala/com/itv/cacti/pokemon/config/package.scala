package com.itv.cacti.pokemon

import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

package object config {

  implicit val portConfigReader: ConfigReader[Port] = ConfigReader[Int].emap {
    int =>
      Port.fromInt(int) match {
        case Some(value) => Right(value)
        case None =>
          Left(CannotConvert(int.toString, "Port", "Its invalid port format"))
      }
  }

  implicit val HostConfigReader: ConfigReader[Host] = ConfigReader[String]
    .emap { str =>
      Host.fromString(str) match {
        case Some(value) => Right(value)
        case None =>
          Left(CannotConvert(str, "Host", "Its invalid ipv4 host format"))
      }
    }

}
