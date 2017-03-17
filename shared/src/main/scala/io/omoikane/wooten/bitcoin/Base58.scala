package io.omoikane.wooten.bitcoin

import io.omoikane.wooten.BaseConversion
import io.omoikane.wooten.error.ByteDeserializationError
import io.omoikane.wooten.impl.implicits._

import scala.annotation.tailrec

/**
  * BitCoin Base58 encoding
  */
object Base58 extends BaseConversion {
  private val base58Characters = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
  private val base58Lookup     = base58Characters.zipWithIndex.toMap

  @tailrec private def computeBase58Characters(input: BigInt, output: List[Char]): String =
    if (input === 0) output.mkString
    else computeBase58Characters(input / 58, base58Characters((input % 58).toInt) :: output)

  def fromBytes(bytes: Seq[Byte]): String =
    bytes.takeWhile(_ === 0.toByte).map(_ => '1').mkString + computeBase58Characters(BigInt(1, bytes.toArray), List.empty[Char])

  @SuppressWarnings(Array("org.wartremover.warts.NoNeedForMonad"))
  def toBytes(characterSequence: Seq[Char]): Either[ByteDeserializationError, Array[Byte]] = {
    val zeroes = characterSequence.takeWhile(_ === '1').map(_ => 0.toByte).toArray
    characterSequence
      .dropWhile(_ === '0')
      .foldLeft[Either[ByteDeserializationError, BigInt]](Right[ByteDeserializationError, BigInt](BigInt(0)))(
        (result: Either[ByteDeserializationError, BigInt], character: Char) =>
          for {
            resultInt <- result
            data      <- base58Lookup.get(character).toRight(ByteDeserializationError(s"'$character' is not a valid Base58 character"))
          } yield resultInt * 58 + data)
      .map(zeroes ++ _.toByteArray.dropWhile(_ === 0.toByte))
  }
}
