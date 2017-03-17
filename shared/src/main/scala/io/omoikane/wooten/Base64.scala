package io.omoikane.wooten
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

import scala.collection.immutable.Queue

object Base64 extends BaseConversion {
  private val base64Characters: String     = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
  private val base64Lookup: Map[Char, Int] = base64Characters.zipWithIndex.toMap

  def fromBytes(bytes: Seq[Byte]): String = {
    val (characters, carry, carryBits) = bytes
      .foldLeft((Queue[Char](), 0, 0))((result: (Queue[Char], Int, Int), byte: Byte) => {
        val (characters, carry, carryBits) = result
        val newCarryBits                   = 2 + carryBits
        val byteValue                      = byte & 0xFF
        val chunk                          = byteValue >>> newCarryBits
        val newCharacter                   = base64Characters((carry << (6 - carryBits)) + chunk)
        val newCarry                       = byteValue - (chunk << newCarryBits)
        if (newCarryBits === 6)
          (characters :+ newCharacter :+ base64Characters(newCarry), 0, 0)
        else
          (characters :+ newCharacter, newCarry, newCarryBits)
      })
    val unpaddedCharacters = if (carryBits === 0) characters else characters :+ base64Characters(carry << (6 - carryBits))
    val remainder          = unpaddedCharacters.length % 4
    if (remainder === 0) unpaddedCharacters.mkString
    else (0 until 4 - remainder).foldLeft(unpaddedCharacters)((result, _) => result :+ '=').mkString
  }

  @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.NoNeedForMonad"))
  def toBytes(characterSequence: Seq[Char]): Either[ByteDeserializationError, Array[Byte]] =
    characterSequence.mkString
      .replaceAll("=+$", "")
      .foldLeft[Either[ByteDeserializationError, (Queue[Byte], Int, Int)]](Right((Queue[Byte](), 0, 0)))(
        (result: Either[ByteDeserializationError, (Queue[Byte], Int, Int)], character: Char) =>
          for {
            resultValues <- result
            data <- base64Lookup
              .get(character)
              .toRight(ByteDeserializationError(s"'$character' is not a valid Base64 character"))
            (bytes, carry, carryBits) = resultValues
          } yield
            if (carryBits === 0)
              (bytes, data, 6)
            else {
              val newCarryBits = carryBits - 2
              val chunk        = data >>> newCarryBits
              (bytes :+ ((carry << (8 - carryBits)) + chunk).toByte, data - (chunk << newCarryBits), newCarryBits)
          }
      )
      .map(_._1.toArray)
}
