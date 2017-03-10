package io.omoikane.wooten

import org.scalatest._
import org.scalacheck.Prop.forAll
import org.scalatest.prop.Checkers

import scala.util.{Left, Right}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class HexTest extends FreeSpec with Matchers with Checkers {
  "Seq(1,2,3)" in {
    Hex.fromBytes(Seq(1, 2, 3).map(_.toByte)) should be("010203")
  }

  "Can serialize and deserialize random bytes" in {
    forAll { (byteArray: Array[Byte]) =>
      Hex.toBytes(Hex.fromBytes(byteArray)) match {
        case Left(_)         => false
        case Right(newArray) => newArray === byteArray
      }
    }
  }
}
