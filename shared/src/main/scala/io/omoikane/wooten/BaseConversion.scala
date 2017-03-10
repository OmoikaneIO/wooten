package io.omoikane.wooten

import io.omoikane.wooten.error.ByteDeserializationError

trait BaseConversion {
  def fromBytes(bytes: Seq[Byte]): String
  def toBytes(string: String): Either[ByteDeserializationError, Array[Byte]]
}
