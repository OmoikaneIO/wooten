package io.omoikane.wooten
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

import scala.collection.immutable.Queue

object Base64 extends BaseConversion {
  private val base64Characters: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
  override def fromBytes(bytes: Seq[Byte]): String = {
    val (characters, carry, carryBits) = bytes
      .foldLeft((Queue[Char](), 0, 0))((result: (Queue[Char], Int, Int), byte: Byte) => {
        val (characters, carry, carryBits) = result
        val newCarryBits                   = 2 + carryBits
        val chunk                          = byte >> newCarryBits
        val newCharacter                   = base64Characters((carry << (6 - carryBits)) + chunk)
        val newCarry                       = byte - (chunk << newCarryBits)
        if (newCarryBits === 6)
          (characters :+ newCharacter :+ base64Characters(newCarry), 0, 0)
        else
          (characters :+ newCharacter, newCarry, newCarryBits)
      })
    val unpadded          = if (carryBits === 0) characters else characters :+ base64Characters(carry << (6 - carryBits))
    val remainder         = unpadded.length % 4
    val paddingChars: Int = if (remainder === 0) 0 else 4 - remainder
    (0 until paddingChars).foldLeft(unpadded)((result: Queue[Char], _: Int) => result :+ '=').mkString
  }

  override def toBytes(string: String): Either[ByteDeserializationError, Array[Byte]] = ???
}
