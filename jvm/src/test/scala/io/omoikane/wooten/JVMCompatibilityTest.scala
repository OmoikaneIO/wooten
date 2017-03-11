package io.omoikane.wooten

import org.scalatest._
import org.scalacheck.Prop.forAll
import org.scalatest.prop.Checkers
import javax.xml.bind.DatatypeConverter

import scala.util.{Left, Right}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class JVMCompatibilityTest extends FreeSpec with Matchers with Checkers {
  "Hex" - {
    "fromBytes agrees with printHexBinary" in {
      forAll { (byteArray: Array[Byte]) =>
        DatatypeConverter.printHexBinary(byteArray) === Hex.fromBytes(byteArray)
      }
    }

    "toBytes is the left inverse of printHexBinary" in {
      forAll { (byteArray: Array[Byte]) =>
        Hex.toBytes(DatatypeConverter.printHexBinary(byteArray)) match {
          case Left(_)       => false
          case Right(parsed) => parsed === byteArray
        }
      }
    }
  }

  "Base64" - {
    "fromBytes agrees with printBase64Binary" in {
      forAll { (byteArray: Array[Byte]) =>
        DatatypeConverter.printBase64Binary(byteArray) === Base64.fromBytes(byteArray)
      }
    }
  }
}
