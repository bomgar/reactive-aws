package com.github.bomgar.auth


import java.net.URL
import java.time.Instant

import com.github.bomgar.auth.credentials.{AwsCredentials, AwsCredentialsProvider}
import org.slf4j.LoggerFactory
import play.api.libs.ws.{WSRequest, WSSignatureCalculator}


class AWS4SignerForAuthorizationHeader(val awsCredentialsProvider: AwsCredentialsProvider, val regionName: String, val serviceName: String)
  extends AWS4SignerBase
  with WSSignatureCalculator {

  val log = LoggerFactory.getLogger(getClass)

  override def sign(request: WSRequest): Unit = {
    val awsCredentials = awsCredentialsProvider.awsCredentials
    val requestDate: Instant = Instant.now()
    val endpointUrl = new URL(request.url)

    // convert to ISO 8601 format for use in signature generation
    val dateTimeStamp: String = dateTimeFormat.format(requestDate)
    request.setHeader("x-amz-date", dateTimeStamp)

    addHostHeader(request, endpointUrl)

    val headers = removeMultiples(request.allHeaders)
    val queryParameters = removeMultiples(request.queryString)

    val canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers)
    val canonicalizedHeaders = getCanonicalizedHeaderString(headers)
    val canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters)

    val bodyHash = toHex(hash(request.getBody.getOrElse(Array.empty)))

    val canonicalRequest = getCanonicalRequest(endpointUrl, request.method, canonicalizedQueryParameters, canonicalizedHeaderNames, canonicalizedHeaders, bodyHash)
    log.debug("Canonical request: {}", canonicalRequest)

    val dateStamp = dateStampFormat.format(requestDate)
    val scope = dateStamp + "/" + regionName + "/" + serviceName + "/" + TERMINATOR
    val stringToSign = getStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalRequest)

    log.debug("String to sign: {}", stringToSign)

    val authorizationHeader: String = createAuthorizationHeader(awsCredentials, canonicalizedHeaderNames, dateStamp, scope, stringToSign)
    request.setHeader("Authorization", authorizationHeader)

  }

  private def createAuthorizationHeader(awsCredentials: AwsCredentials, canonicalizedHeaderNames: String, dateStamp: String, scope: String, stringToSign: String): String = {
    // compute the signing key
    val kSecret: Array[Byte] = (SCHEME + awsCredentials.awsSecretKey).getBytes
    val kDate: Array[Byte] = sign(dateStamp, kSecret, "HmacSHA256")
    val kRegion: Array[Byte] = sign(regionName, kDate, "HmacSHA256")
    val kService: Array[Byte] = sign(serviceName, kRegion, "HmacSHA256")
    val kSigning: Array[Byte] = sign(TERMINATOR, kService, "HmacSHA256")
    val signature: Array[Byte] = sign(stringToSign, kSigning, "HmacSHA256")

    val credentialsAuthorizationHeader = "Credential=" + awsCredentials.awsAccessKeyId + "/" + scope
    val signedHeadersAuthorizationHeader = "SignedHeaders=" + canonicalizedHeaderNames
    val signatureAuthorizationHeader = "Signature=" + toHex(signature)

    val authorizationHeader = SCHEME + "-" + ALGORITHM + " " + credentialsAuthorizationHeader + ", " + signedHeadersAuthorizationHeader + ", " + signatureAuthorizationHeader
    authorizationHeader
  }

  private def removeMultiples(multiMap: Map[String, Seq[String]]): Map[String, String] = {
    multiMap.toSeq.flatMap{
      case (key, values) =>
        values.map(value => (key,value))
    }.toMap
  }

  private def addHostHeader(request: WSRequest, endpointUrl: URL): Unit = {
    val port: Int = endpointUrl.getPort
    request.setHeader("Host",
      if (port > -1) {
        endpointUrl.getHost.concat(":" + Integer.toString(port))
      } else {
        endpointUrl.getHost
      }
    )
  }
}
