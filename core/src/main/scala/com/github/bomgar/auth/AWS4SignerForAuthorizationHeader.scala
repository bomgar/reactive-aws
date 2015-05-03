package com.github.bomgar.auth


import java.net.URL
import java.time.{Clock, Instant}

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{AwsCredentials, AwsCredentialsProvider}
import com.github.bomgar.utils.BinaryUtils
import org.slf4j.LoggerFactory
import play.api.libs.ws.{WSRequest, WSSignatureCalculator}

import scala.util.Try


/**
 * class copied from aws examples and converted to scala (not nice yet)
 */
class AWS4SignerForAuthorizationHeader(
                                        val awsCredentialsProvider: AwsCredentialsProvider,
                                        val region: Region.Type,
                                        val serviceName: String,
                                        val clock: Clock = java.time.Clock.systemUTC()
                                        )
  extends AWS4Signer
  with WSSignatureCalculator {

  val log = LoggerFactory.getLogger(getClass)

  override def sign(request: WSRequest): Unit = {
    val awsCredentials = awsCredentialsProvider.awsCredentials
    val requestDate: Instant = Instant.now(clock)
    val endpointUrl = new URL(request.url)
    val dateTimeStamp: String = dateTimeFormat.format(requestDate)

    val headers = removeMultiples(request.allHeaders)
    val additionalHeadersToSign = computeAdditionalHeaders(endpointUrl, dateTimeStamp)

    val queryString = Try(request.queryString).toOption.getOrElse(Map.empty[String, Seq[String]])

    val queryParameters = removeMultiples(queryString)

    val bodyHash = BinaryUtils.toHex(BinaryUtils.hash(request.getBody.getOrElse(Array.empty)))

    val canonicalRequest = new CanonicalRequest(new URL(request.url), request.method, headers ++ additionalHeadersToSign, queryParameters, bodyHash)

    log.debug("Canonical request: {}", canonicalRequest)

    val dateStamp = dateStampFormat.format(requestDate)
    val scope = dateStamp + "/" + region.toString + "/" + serviceName + "/" + TERMINATOR
    val stringToSign = buildStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalRequest)

    log.debug("String to sign: {}", stringToSign)

    val authorizationHeader: String = createAuthorizationHeader(awsCredentials, canonicalRequest.canonicalizedHeaderNames, dateStamp, scope, stringToSign)
    applyHeadersToRequest(request, additionalHeadersToSign + ("Authorization" -> authorizationHeader))
  }

  private def computeAdditionalHeaders(endpointUrl: URL, timestamp: String): Map[String, String] = {
    val port: Int = endpointUrl.getPort
    val hostHeader: String = createHostHeader(endpointUrl, port)
    Map(
      "Host" -> hostHeader,
      "x-amz-date" -> timestamp
    )
  }

  private def applyHeadersToRequest(request: WSRequest, headers: Map[String, String]): WSRequest = {

    val headersIterator: Iterator[(String, String)] = headers.iterator

    def addHeader(request: WSRequest, header: (String,String)): WSRequest = {
      val (key, value) = header
      request.setHeader(key, value)
    }

    headersIterator.foldLeft(request)(addHeader)
  }

  private def createHostHeader(endpointUrl: URL, port: Int): String = {
    if (port > -1) {
      endpointUrl.getHost.concat(":" + Integer.toString(port))
    } else {
      endpointUrl.getHost
    }
  }

  private def createAuthorizationHeader(awsCredentials: AwsCredentials, canonicalizedHeaderNames: String, dateStamp: String, scope: String, stringToSign: String): String = {
    // compute the signing key
    val kSecret: Array[Byte] = (SCHEME + awsCredentials.awsSecretKey).getBytes
    val kDate: Array[Byte] = sign(dateStamp, kSecret, "HmacSHA256")
    val kRegion: Array[Byte] = sign(region.toString, kDate, "HmacSHA256")
    val kService: Array[Byte] = sign(serviceName, kRegion, "HmacSHA256")
    val kSigning: Array[Byte] = sign(TERMINATOR, kService, "HmacSHA256")
    val signature: Array[Byte] = sign(stringToSign, kSigning, "HmacSHA256")

    val credentialsAuthorizationHeader = "Credential=" + awsCredentials.awsAccessKeyId + "/" + scope
    val signedHeadersAuthorizationHeader = "SignedHeaders=" + canonicalizedHeaderNames
    val signatureAuthorizationHeader = "Signature=" + BinaryUtils.toHex(signature)

    val authorizationHeader = SCHEME + "-" + ALGORITHM + " " + credentialsAuthorizationHeader + ", " + signedHeadersAuthorizationHeader + ", " + signatureAuthorizationHeader
    authorizationHeader
  }

  private def removeMultiples(multiMap: Map[String, Seq[String]]): Map[String, String] = {
    multiMap.toSeq.flatMap {
      case (key, values) =>
        values.map(value => (key, value))
    }.toMap
  }

}
