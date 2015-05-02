package com.github.bomgar.utils

import java.nio.charset.StandardCharsets

import org.specs2.mutable.Specification


class BinaryUtilsTest extends Specification {
  "binary utils" should {
    "calculate hashes" in {
      val text: String = "Hello World!"
      val bytes = text.getBytes(StandardCharsets.UTF_8)
      BinaryUtils.toHex(BinaryUtils.hash(bytes)) must be equalTo "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
      BinaryUtils.toHex(BinaryUtils.hash(text)) must be equalTo "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
    }
  }
}
