package com.github.bomgar.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Locale


object BinaryUtils {
  /**
   * Hashes the string contents (assumed to be UTF-8) using the SHA-256
   * algorithm.
   */
  def hash(text: String): Array[Byte] = hash(text.getBytes(StandardCharsets.UTF_8))

  /**
   * Hashes the byte array using the SHA-256 algorithm.
   */
  def hash(data: Array[Byte]): Array[Byte] = {
    try {
      val md: MessageDigest = MessageDigest.getInstance("SHA-256")
      md.update(data)
      md.digest
    }
    catch {
      case e: Exception =>
        throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage, e)
    }
  }

  def toHex(data: Array[Byte]): String = {
    data.map("%02X" format _).mkString.toLowerCase(Locale.getDefault)
  }
}
