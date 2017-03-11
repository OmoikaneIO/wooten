package io.omoikane.wooten
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

import scala.collection.immutable.Queue

object Hex extends BaseConversion {
  private val hexCharacters: String     = "0123456789ABCDEF"
  private val hexLookup: Map[Char, Int] = hexCharacters.zipWithIndex.toMap

  override def fromBytes(bytes: Seq[Byte]): String =
    bytes.flatMap((byte: Byte) => Seq(hexCharacters(byte >>> 4), hexCharacters(byte & 0x0F))).mkString

  @SuppressWarnings(Array("org.wartremover.warts.NoNeedForMonad"))
  def toBytes(string: String): Either[ByteDeserializationError, Array[Byte]] =
    if (string.length % 2 === 1)
      Left[ByteDeserializationError, Array[Byte]](
        ByteDeserializationError(s"Input must be of even length, was of length ${string.length}"))
    else {
      val characters: Seq[Char] = string.replaceAll("^0x", "").toList
      characters
        .zip(characters.drop(1))
        .foldLeft[Either[ByteDeserializationError, Queue[Byte]]](Right[ByteDeserializationError, Queue[Byte]](Queue[Byte]()))(
          (result: Either[ByteDeserializationError, Queue[Byte]], characterPair: (Char, Char)) =>
            for {
              bytes <- result
              (firstCharacter, secondCharacter) = characterPair
              firstHalfByte <- hexLookup
                .get(firstCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$firstCharacter' is not a valid hexadecimal character"))
              secondHalfByte <- hexLookup
                .get(secondCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$secondCharacter' is not a valid hexadecimal character"))
            } yield bytes :+ ((firstHalfByte << 4) + secondHalfByte).toByte)
        .map(_.toArray)
    }

}
