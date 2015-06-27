package com.github.bomgar.auth


import java.net.URL
import java.time.{Clock, Instant}

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{AwsCredentials, AwsCredentialsProvider}
import com.github.bomgar.utils.BinaryUtils
import com.ning.http.client.{Request, RequestBuilderBase, SignatureCalculator}
import org.slf4j.LoggerFactory
import play.api.libs.ws._

import scala.collection.JavaConverters._
import scala.language.existentials


/**
 * class copied from aws examples and converted to scala (not nice yet)
 */
abstract class AWS4SignerForAuthorizationHeader(
                                        val awsCredentialsProvider: AwsCredentialsProvider,
                                        val region: Region.Type,
                                        val serviceName: String,
                                        val clock: Clock = java.time.Clock.systemUTC()
                                        )
  extends AWS4Signer
  with WSSignatureCalculator with SignatureCalculator {

  val log = LoggerFactory.getLogger(getClass)

  def calculateAndAddSignature(request: Request, requestBuilderBase: RequestBuilderBase[_], body: Array[Byte]): Unit = {
    val awsCredentials = awsCredentialsProvider.awsCredentials
    val requestDate: Instant = Instant.now(clock)
    val endpointUrl = new URL(request.getUrl)
    val dateTimeStamp: String = dateTimeFormat.format(requestDate)

    val headers = removeMultiples(request.getHeaders.iterator().asScala.map(entry => entry.getKey -> entry.getValue.asScala.toSeq).toMap)
    val additionalHeadersToSign = computeAdditionalHeaders(endpointUrl, dateTimeStamp)

    val queryParameters = request.getQueryParams.asScala.map(param => param.getName -> param.getValue).toMap

    val bodyHash = BinaryUtils.toHex(BinaryUtils.hash(body))

    val canonicalRequest = new CanonicalRequest(new URL(request.getUrl), request.getMethod, headers ++ additionalHeadersToSign, queryParameters, bodyHash)

    log.debug("Canonical request: {}", canonicalRequest)

    val dateStamp = dateStampFormat.format(requestDate)
    val scope = dateStamp + "/" + region.toString + "/" + serviceName + "/" + TERMINATOR
    val stringToSign = buildStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalRequest)

    log.debug("String to sign: {}", stringToSign)

    val authorizationHeader: String = createAuthorizationHeader(awsCredentials, canonicalRequest.canonicalizedHeaderNames, dateStamp, scope, stringToSign)
    for(additionalHeader <- additionalHeadersToSign + ("Authorization" -> authorizationHeader)) {
      requestBuilderBase.setHeader(additionalHeader._1, additionalHeader._2)
    }
  }

  private def computeAdditionalHeaders(endpointUrl: URL, timestamp: String): Map[String, String] = {
    val port: Int = endpointUrl.getPort
    val hostHeader: String = createHostHeader(endpointUrl, port)
    Map(
      "Host" -> hostHeader,
      "x-amz-date" -> timestamp
    )
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
