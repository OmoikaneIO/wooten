package io.omoikane.wooten

import java.nio.charset.StandardCharsets.UTF_8

import org.scalatest._

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Base64Test extends FreeSpec with Matchers {
  "\"Man\"" in {
    Base64.fromBytes("Man".getBytes(UTF_8)) should be("TWFu")
  }

  "\"M\"" in {
    Base64.fromBytes("M".getBytes(UTF_8)) should be("TQ==")
  }

  "\"Ma\"" in {
    Base64.fromBytes("Ma".getBytes(UTF_8)) should be("TWE=")
  }
}
