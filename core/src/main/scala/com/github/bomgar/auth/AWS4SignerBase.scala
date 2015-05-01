package com.github.bomgar.auth

import java.net.{URL, URLEncoder}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import scala.collection.immutable.TreeMap

abstract class AWS4SignerBase {

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



  /**
   * Returns the canonical collection of header names that will be included in
   * the signature. For AWS4, all header names must be included in the process
   * in sorted canonicalized order.
   */
  protected def getCanonicalizeHeaderNames(headers: Map[String, String]): String = {
    headers
      .map(_._1)
      .toList
      .sortWith(_.toLowerCase < _.toLowerCase)
      .mkString(";")
  }

  /**
   * Computes the canonical headers with values for the request. For AWS4, all
   * headers must be included in the signing process.
   */
  protected def getCanonicalizedHeaderString(headers: Map[String, String]): String = {
    headers
      .map(_._1)
      .toList
      .sortWith(_.toLowerCase < _.toLowerCase)
      .map(key => key.toLowerCase.replaceAll("\\s+", " ") + ":" + headers(key).replaceAll("\\s+", " ") + "\n")
      .mkString
  }

  /**
   * Returns the canonical request string to go into the signer process; this
       consists of several canonical sub-parts.
   * @return
   */
  protected def getCanonicalRequest(endpoint: URL, httpMethod: String, queryParameters: String, canonicalizedHeaderNames: String, canonicalizedHeaders: String, bodyHash: String): String = {
    httpMethod + "\n" +
      getCanonicalizedResourcePath(endpoint) + "\n" +
      queryParameters + "\n" +
      canonicalizedHeaders + "\n" +
      canonicalizedHeaderNames + "\n" +
      bodyHash
  }

  /**
   * Returns the canonicalized resource path for the service endpoint.
   */
  protected def getCanonicalizedResourcePath(endpoint: URL): String = {
    Option(endpoint)
      .map(_.getPath)
      .filter(_.isEmpty)
      .map(path => urlEncode(path, keepPathSlash = true))
      .map(encodedPath =>
      if (encodedPath.startsWith("/")) encodedPath
      else "/".concat(encodedPath)
      )
      .getOrElse("/")
  }

  /**
   * Examines the specified query string parameters and returns a
   * canonicalized form.
   * <p>
   * The canonicalized query string is formed by first sorting all the query
   * string parameters, then URI encoding both the key and value and then
   * joining them, in order, separating key value pairs with an '&'.
   *
   * @param parameters
     * The query string parameters to be canonicalized.
   *
   * @return A canonicalized form for the specified query string parameters.
   */
  def getCanonicalizedQueryString(parameters: Map[String, String]): String = {

    val sorted = TreeMap(
      parameters.toSeq.map {
        case (key, value) =>
          (urlEncode(key, keepPathSlash = false), urlEncode(value, keepPathSlash = false))
      }: _*
    )
    sorted.map {
      case (key, value) => s"$key=$value"
    }.mkString("&")
  }

  protected def getStringToSign(scheme: String, algorithm: String, dateTime: String, scope: String, canonicalRequest: String): String = {
    scheme + "-" + algorithm + "\n" + dateTime + "\n" + scope + "\n" + toHex(hash(canonicalRequest))
  }

  /**
   * Hashes the string contents (assumed to be UTF-8) using the SHA-256
   * algorithm.
   */
  protected def hash(text: String): Array[Byte] = {
    try {
      val md: MessageDigest = MessageDigest.getInstance("SHA-256")
      md.update(text.getBytes("UTF-8"))
      md.digest
    }
    catch {
      case e: Exception =>
        throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage, e)
    }
  }

  /**
   * Hashes the byte array using the SHA-256 algorithm.
   */
  protected def hash(data: Array[Byte]): Array[Byte] = {
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

  protected def sign(stringData: String, key: Array[Byte], algorithm: String): Array[Byte] = {
    try {
      val data: Array[Byte] = stringData.getBytes("UTF-8")
      val mac: Mac = Mac.getInstance(algorithm)
      mac.init(new SecretKeySpec(key, algorithm))
      mac.doFinal(data)
    }
    catch {
      case e: Exception =>
        throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage, e)
    }
  }

  protected def urlEncode(url: String, keepPathSlash: Boolean): String = {
    val encoded: String = URLEncoder.encode(url, StandardCharsets.UTF_8.toString)
    if (keepPathSlash) {
      encoded.replace("%2F", "/")
    } else {
      encoded
    }
  }

  protected def toHex(data: Array[Byte]): String = {
    data.map("%02X" format _).mkString.toLowerCase(Locale.getDefault)
  }



}
