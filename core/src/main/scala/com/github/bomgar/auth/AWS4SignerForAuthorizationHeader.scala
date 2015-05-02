package com.github.bomgar.auth


import java.net.URL
import java.time.Instant

import akka.actor.Status.Success
import com.github.bomgar.auth.credentials.{AwsCredentials, AwsCredentialsProvider}
import org.slf4j.LoggerFactory
import play.api.libs.ws.{WSRequest, WSSignatureCalculator}
import com.github.bomgar.Region

import scala.util.Try


class AWS4SignerForAuthorizationHeader(val awsCredentialsProvider: AwsCredentialsProvider, val region: Region.Type, val serviceName: String)
  extends AWS4SignerBase
  with WSSignatureCalculator {

  val log = LoggerFactory.getLogger(getClass)

  override def sign(request: WSRequest): Unit = {
    var requestVar = request
    val awsCredentials = awsCredentialsProvider.awsCredentials
    val requestVarDate: Instant = Instant.now()
    val endpointUrl = new URL(requestVar.url)

    var headers = removeMultiples(requestVar.allHeaders)

    // convert to ISO 8601 format for use in signature generation
    val dateTimeStamp: String = dateTimeFormat.format(requestVarDate)
    requestVar = requestVar.setHeader("x-amz-date", dateTimeStamp)
    headers += ("x-amz-date" -> dateTimeStamp)


    val port: Int = endpointUrl.getPort
    val hostHeader: String = createHostHeader(endpointUrl, port)
    requestVar = requestVar.setHeader("Host", hostHeader)
    headers += ("Host" -> hostHeader)



    val queryString = Try(requestVar.queryString).toOption.getOrElse(Map.empty[String, Seq[String]])

    val queryParameters = removeMultiples(queryString)

    val canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers)
    val canonicalizedHeaders = getCanonicalizedHeaderString(headers)
    val canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters)

    val bodyHash = toHex(hash(requestVar.getBody.getOrElse(Array.empty)))

    val canonicalrequestVar = getCanonicalRequest(endpointUrl, requestVar.method, canonicalizedQueryParameters, canonicalizedHeaderNames, canonicalizedHeaders, bodyHash)
    log.debug("Canonical requestVar: {}", canonicalrequestVar)

    val dateStamp = dateStampFormat.format(requestVarDate)
    val scope = dateStamp + "/" + region.toString + "/" + serviceName + "/" + TERMINATOR
    val stringToSign = getStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalrequestVar)

    log.debug("String to sign: {}", stringToSign)

    val authorizationHeader: String = createAuthorizationHeader(awsCredentials, canonicalizedHeaderNames, dateStamp, scope, stringToSign)
    requestVar = request.setHeader("Authorization", authorizationHeader)

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

}
