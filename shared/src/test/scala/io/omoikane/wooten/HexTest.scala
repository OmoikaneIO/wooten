package io.omoikane.wooten

import io.omoikane.wooten.error.ByteDeserializationError
import org.scalatest._
import org.scalatest.prop.Checkers

import scala.util.{Left, Right}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class HexTest extends FreeSpec with Matchers with Checkers {
  "Seq(1,2,3)" in {
    Hex.fromBytes(Seq(1, 2, 3).map(_.toByte)) should be("010203")
  }

  "Seq(0,0)" in {
    Hex.fromBytes(Seq(0, 0).map(_.toByte)) should be("0000")
  }

  "Can deserialize \"0000\"" in {
    Hex.toBytes("0000") match {
      case Left(ByteDeserializationError(message)) => fail(message)
      case Right(parsedArray)                      => parsedArray should be(Array(0, 0).map(_.toByte))
    }
  }

  "Can serialize and deserialize random bytes" in {
    check { (byteArray: Array[Byte]) =>
      Hex.toBytes(Hex.fromBytes(byteArray)) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }

  "Ignores leading 0x" in {
    check { (byteArray: Array[Byte]) =>
      Hex.toBytes("0x" + Hex.fromBytes(byteArray)) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }

  "Handles lower case input" in {
    check { (byteArray: Array[Byte]) =>
      Hex.toBytes(Hex.fromBytes(byteArray).toLowerCase) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }
}
