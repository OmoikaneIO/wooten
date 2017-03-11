package io.omoikane.wooten

import java.nio.charset.StandardCharsets.UTF_8

import io.omoikane.wooten.error.ByteDeserializationError
import org.scalatest._
import org.scalacheck.Prop.forAll

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Base64Test extends FreeSpec with Matchers {
  "fromBytes" - {
    "\"Man\"" in {
      Base64.fromBytes("Man".getBytes(UTF_8)) should be("TWFu")
    }

    "\"M\"" in {
      Base64.fromBytes("M".getBytes(UTF_8)) should be("TQ==")
    }

    "\"Ma\"" in {
      Base64.fromBytes("Ma".getBytes(UTF_8)) should be("TWE=")
    }
  }

  "toBytes" - {
    "\"Man\"" in {
      Base64.toBytes("TWFu") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => bytes should be("Man".getBytes(UTF_8))
      }
    }

    "\"M\"" in {
      Base64.toBytes("TQ==") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => new String(bytes, UTF_8) should be("M")
      }
    }

    "\"Ma\"" in {
      Base64.toBytes("TWE=") match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(bytes)                            => new String(bytes, UTF_8) should be("Ma")
      }
    }
  }

  "Can serialize and deserialize random bytes" in {
    forAll { (byteArray: Array[Byte]) =>
      Base64.toBytes(Base64.fromBytes(byteArray)) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }
}
