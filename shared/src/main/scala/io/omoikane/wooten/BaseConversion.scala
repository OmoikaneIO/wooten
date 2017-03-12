package io.omoikane.wooten

import io.omoikane.wooten.error.ByteDeserializationError

trait BaseConversion {
  def fromBytes(bytes: Seq[Byte]): String
  def toBytes(characterSequence: Seq[Char]): Either[ByteDeserializationError, Array[Byte]]
}
