package com.github.bomgar.auth

import java.net.{URL, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.github.bomgar.utils.BinaryUtils

import scala.collection.immutable.TreeMap


/**
 * class copied from aws examples and converted to scala (not nice yet)
 */
trait AWS4Signer {

  protected val EMPTY_BODY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
  protected val UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD"
  protected val SCHEME = "AWS4"
  protected val ALGORITHM = "HMAC-SHA256"
  protected val TERMINATOR = "aws4_request"

  /** format strings for the date/time and date stamps required during signing **/
  private val ISO8601BasicFormat = "yyyyMMdd'T'HHmmss'Z'"
  private val DateStringFormat = "yyyyMMdd"

  protected val dateTimeFormat: DateTimeFormatter = DateTimeFormatter
    .ofPattern(ISO8601BasicFormat)
    .withZone(ZoneId.of("UTC"))

  protected val dateStampFormat: DateTimeFormatter = DateTimeFormatter
    .ofPattern(DateStringFormat)
    .withZone(ZoneId.of("UTC"))


  protected def buildStringToSign(scheme: String, algorithm: String, dateTime: String, scope: String, canonicalRequest: CanonicalRequest): String = {
    val requestHash: String = BinaryUtils.toHex(BinaryUtils.hash(canonicalRequest.toString))
    s"""|$scheme-$algorithm
        |$dateTime
        |$scope
        |$requestHash""".stripMargin
  }

  protected def sign(stringData: String, key: Array[Byte], algorithm: String): Array[Byte] = {
    try {
      val data: Array[Byte] = stringData.getBytes(StandardCharsets.UTF_8)
      val mac: Mac = Mac.getInstance(algorithm)
      mac.init(new SecretKeySpec(key, algorithm))
      mac.doFinal(data)
    }
    catch {
      case e: Exception =>
        throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage, e)
    }
  }

}

object AWS4Signer {
  val BodyHashHeader = "X-RAWS-BodyHash"

  val base64Encoder = Base64.getEncoder

  val base64Decoder = Base64.getDecoder


}
