package io.omoikane.wooten.bitcoin

import java.nio.charset.StandardCharsets.UTF_8

import io.omoikane.wooten.error.ByteDeserializationError
import org.scalatest._
import org.scalatest.prop.Checkers

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Base58Test extends FreeSpec with Matchers with Checkers {
  "fromBytes" - {
    "Array(1)" in {
      Base58.fromBytes(Array(1.toByte)) should be("2")
    }

    "\"Man\"" in {
      Base58.fromBytes("Man".getBytes(UTF_8)) should be("SzVj")
    }

    "\"M\"" in {
      Base58.fromBytes("M".getBytes(UTF_8)) should be("2L")
    }

    "\"Ma\"" in {
      Base58.fromBytes("Ma".getBytes(UTF_8)) should be("6tY")
    }
  }

  "toBytes" - {
    "Array(1)" in {
      Base58.toBytes("2") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => bytes should be(Array(1.toByte))
      }
    }

    "\"Man\"" in {
      Base58.toBytes("SzVj") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => bytes should be("Man".getBytes(UTF_8))
      }
    }

    "\"M\"" in {
      Base58.toBytes("2L") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => new String(bytes, UTF_8) should be("M")
      }
    }

    "\"Ma\"" in {
      Base58.toBytes("6tY") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => new String(bytes, UTF_8) should be("Ma")
      }
    }
  }

  "Can serialize and deserialize random bytes" in {
    check { (byteArray: Array[Byte]) =>
      Base58.toBytes(Base58.fromBytes(byteArray)) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }
}
