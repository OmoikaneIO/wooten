package io.omoikane.wooten

import org.scalatest._
import org.scalatest.prop.Checkers
import javax.xml.bind.DatatypeConverter

import io.omoikane.wooten.error.ByteDeserializationError

import scala.util.{Left, Right}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class JVMCompatibilityTest extends FreeSpec with Matchers with Checkers {

  "Hex" - {
    "fromBytes agrees with printHexBinary" in {
      check { (byteArray: Array[Byte]) =>
        DatatypeConverter.printHexBinary(byteArray) === Hex.fromBytes(byteArray)
      }
    }

    "toBytes is the left inverse of printHexBinary" in {
      check { (byteArray: Array[Byte]) =>
        Hex.toBytes(DatatypeConverter.printHexBinary(byteArray)) match {
          case Left(_)       => false
          case Right(parsed) => parsed === byteArray
        }
      }
    }
  }

  "Base64" - {
    "fromBytes agrees with printBase64Binary" in {
      check { (byteArray: Array[Byte]) =>
        DatatypeConverter.printBase64Binary(byteArray) === Base64.fromBytes(byteArray)
      }
    }

    "toBytes is the left inverse of printBase64Binary" in {
      check { (byteArray: Array[Byte]) =>
        Base64.toBytes(DatatypeConverter.printBase64Binary(byteArray)) match {
          case Left(_)       => false
          case Right(parsed) => parsed === byteArray
        }
      }
    }
  }

  "Base64URL" - {
    val encoder = java.util.Base64.getUrlEncoder

    "Encode Array(-1)" in {
      val input = Array(-1).map(_.toByte)
      Base64URL.fromBytes(input) should be(encoder.encodeToString(input))
    }

    "Encode and Decode Array(-1)" in {
      val input = Array(-1).map(_.toByte)
      Base64URL.toBytes(encoder.encodeToString(input)) match {
        case Left(ByteDeserializationError(message)) => fail(message)
        case Right(parsedValue)                      => parsedValue should be(input)
      }
    }

    "Encode Array(-5)" in {
      val input = Array(-5).map(_.toByte)
      Base64URL.fromBytes(input) should be(encoder.encodeToString(input))
    }

    "fromBytes agrees with java.util.Base64.getUrlEncoder" in {
      check { (byteArray: Array[Byte]) =>
        encoder.encodeToString(byteArray) === Base64URL.fromBytes(byteArray)
      }
    }

    "toBytes is the left inverse of java.util.Base64.getUrlEncoder" in {
      check { (byteArray: Array[Byte]) =>
        Base64URL.toBytes(encoder.encodeToString(byteArray)) match {
          case Left(_)       => false
          case Right(parsed) => parsed === byteArray
        }
      }
    }
  }
}
