package com.github.bomgar.auth

import java.net.{URLEncoder, URL}
import java.nio.charset.StandardCharsets

import scala.collection.immutable.TreeMap


class CanonicalRequest(val endpoint: URL, val httpMethod: String, headers: Map[String, String], queryParameters: Map[String, String], val bodyHash: String) {

  val canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers)

  val canonicalizedHeaders = getCanonicalizedHeaderString(headers)

  val canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters)

  override def toString =  httpMethod + "\n" +
    getCanonicalizedResourcePath(endpoint) + "\n" +
    canonicalizedQueryParameters + "\n" +
    canonicalizedHeaders + "\n" +
    canonicalizedHeaderNames + "\n" +
    bodyHash

  private def urlEncode(url: String, keepPathSlash: Boolean): String = {
    val encoded: String = URLEncoder.encode(url, StandardCharsets.UTF_8.toString)
    if (keepPathSlash) {
      encoded.replace("%2F", "/")
    } else {
      encoded
    }
  }

  /**
   * Returns the canonical collection of header names that will be included in
   * the signature. For AWS4, all header names must be included in the process
   * in sorted canonicalized order.
   */
  private def getCanonicalizeHeaderNames(headers: Map[String, String]): String = {
    headers
      .map(_._1)
      .toList
      .map(_.toLowerCase)
      .sorted
      .mkString(";")
  }

  /**
   * Computes the canonical headers with values for the request. For AWS4, all
   * headers must be included in the signing process.
   */
  private def getCanonicalizedHeaderString(headers: Map[String, String]): String = {
    headers
      .map(_._1)
      .toList
      .sortWith(_.toLowerCase < _.toLowerCase)
      .map(key => key.toLowerCase.replaceAll("\\s+", " ") + ":" + headers(key).replaceAll("\\s+", " ") + "\n")
      .mkString
  }


  /**
   * Returns the canonicalized resource path for the service endpoint.
   */
  private def getCanonicalizedResourcePath(endpoint: URL): String = {
    Option(endpoint)
      .map(_.getPath)
      .filter(!_.isEmpty)
      .map(path => urlEncode(path, keepPathSlash = true))
      .map(
        encodedPath =>
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
  private def getCanonicalizedQueryString(parameters: Map[String, String]): String = {

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

}
