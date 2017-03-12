package io.omoikane.wooten

import io.omoikane.wooten.error.ByteDeserializationError

object Base64URL extends BaseConversion {
  def fromBytes(bytes: Seq[Byte]): String = Base64.fromBytes(bytes) map {
    case '/' => '_'
    case '+' => '-'
    case c   => c
  }

  def toBytes(characterSequence: Seq[Char]): Either[ByteDeserializationError, Array[Byte]] =
    Base64.toBytes(characterSequence map {
      case '_' => '/'
      case '-' => '+'
      case c   => c
    })
}
