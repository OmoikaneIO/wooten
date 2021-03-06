package io.omoikane.wooten
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

import scala.collection.immutable.Queue

object Hex extends BaseConversion {
  private val hexCharacters: String     = "0123456789ABCDEF"
  private val hexLookup: Map[Char, Int] = hexCharacters.zipWithIndex.toMap

  def fromBytes(bytes: Seq[Byte]): String =
    bytes.flatMap((byte: Byte) => Seq(hexCharacters((byte & 0xF0) >>> 4), hexCharacters(byte & 0x0F))).mkString

  @SuppressWarnings(Array("org.wartremover.warts.NoNeedForMonad"))
  def toBytes(characterSequence: Seq[Char]): Either[ByteDeserializationError, Array[Byte]] =
    if (characterSequence.length % 2 === 1)
      Left[ByteDeserializationError, Array[Byte]](
        ByteDeserializationError(s"Input must be of even length, was of length ${characterSequence.length}"))
    else {
      val characters: Seq[Char] = characterSequence.mkString.replaceAll("^0x", "")
      characters
        .sliding(2, 2)
        .foldLeft[Either[ByteDeserializationError, Queue[Byte]]](Right[ByteDeserializationError, Queue[Byte]](Queue[Byte]()))(
          (result: Either[ByteDeserializationError, Queue[Byte]], characterPair: Seq[Char]) =>
            for {
              bytes          <- result
              firstCharacter <- characterPair.headOption.toRight(ByteDeserializationError("Parse error"))
              firstHalfByte <- hexLookup
                .get(firstCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$firstCharacter' is not a valid hexadecimal character"))
              secondCharacter <- characterPair.drop(1).headOption.toRight(ByteDeserializationError("Parse error"))
              secondHalfByte <- hexLookup
                .get(secondCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$secondCharacter' is not a valid hexadecimal character"))
            } yield bytes :+ ((firstHalfByte << 4) + secondHalfByte).toByte)
        .map(_.toArray)
    }

}
