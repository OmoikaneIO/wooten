package io.omoikane.wooten
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

object Hex extends BaseConversion {
  private val hexCharacters: String     = "0123456789ABCDEF"
  private val hexLookup: Map[Char, Int] = hexCharacters.zipWithIndex.toMap

  override def fromBytes(bytes: Seq[Byte]): String =
    bytes.flatMap((byte: Byte) => Seq(hexCharacters((byte & 0xF0) >> 4), hexCharacters(byte & 0xF))).mkString

  @SuppressWarnings(Array("org.wartremover.warts.NoNeedForMonad"))
  def toBytes(string: String): Either[ByteDeserializationError, Array[Byte]] =
    if (string.length % 2 === 1)
      Left[ByteDeserializationError, Array[Byte]](
        ByteDeserializationError(s"Input must be of even length, was of length ${string.length}"))
    else {
      val characters: Seq[Char] = string.toList
      characters
        .zip(characters.drop(1))
        .foldRight[Either[ByteDeserializationError, List[Byte]]](Right[ByteDeserializationError, List[Byte]](List.empty[Byte]))(
          (characterPair: (Char, Char), acc: Either[ByteDeserializationError, List[Byte]]) =>
            for {
              bytes <- acc
              (firstCharacter, secondCharacter) = characterPair
              firstHalfByte <- hexLookup
                .get(firstCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$firstCharacter' is not a valid hexadecimal character"))
              secondHalfByte <- hexLookup
                .get(secondCharacter.toUpper)
                .toRight(ByteDeserializationError(s"'$secondCharacter' is not a valid hexadecimal character"))
            } yield ((firstHalfByte << 4) + secondHalfByte).toByte :: bytes)
        .map(_.toArray)
    }

}
