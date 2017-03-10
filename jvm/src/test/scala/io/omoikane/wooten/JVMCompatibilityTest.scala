package io.omoikane.wooten

import org.scalatest._
import org.scalacheck.Prop.forAll
import org.scalatest.prop.Checkers
import javax.xml.bind.DatatypeConverter

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class JVMCompatibilityTest extends FreeSpec with Matchers with Checkers {
  "Same behavior as DatatypeConverter.printHexBinary on random bytes" in {
    forAll { (byteArray: Array[Byte]) =>
      DatatypeConverter.printHexBinary(byteArray) === Hex.fromBytes(byteArray)
    }
  }
}
