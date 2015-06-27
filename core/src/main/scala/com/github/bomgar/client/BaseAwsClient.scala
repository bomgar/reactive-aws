package com.github.bomgar.client

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.github.bomgar.Region
import com.github.bomgar.auth.InMemoryBodyAWS4SignerForAuthorizationHeader
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import org.slf4j.LoggerFactory
import play.api.http.Status._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Elem

class BaseAwsClient(
                     val credentialsProvider: AwsCredentialsProvider,
                     val region: Region.Type,
                     val client: WSClient,
                     val serviceName: String,
                     val defaultTimeout: Duration
                     )(implicit executionContext: ExecutionContext) {

  val log = LoggerFactory.getLogger(getClass)

  val baseUrl = s"https://$serviceName.$region.amazonaws.com"

  protected def executeFormEncodedAction(actionParameters: Map[String, String], url: String = baseUrl, timeout: Duration = defaultTimeout): Future[Elem] = {
    val body: Array[Byte] = encodeParameters(actionParameters).getBytes(StandardCharsets.UTF_8)

    val response = client
      .url(url)
      .withHeaders(
        "Content-Type" -> "application/x-www-form-urlencoded"
      )
      .sign(new InMemoryBodyAWS4SignerForAuthorizationHeader(credentialsProvider, region, serviceName, body = body))
      .withRequestTimeout(timeout.toMillis.toInt)
      .post(body)

    extractFromResponse(response)(_.xml)
  }

  private def extractFromResponse[T](responseFuture: Future[WSResponse])(extractor: (WSResponse => T)): Future[T] = {
    responseFuture.flatMap { response =>
      log.debug("AWS response status: '{}' body: '{}'", response.status, response.body)
      if (response.status == OK) {
        Future.successful(extractor(response))
      } else {
        Future.failed(AwsCallFailedException.fromErrorResponse(response.status, response.body))
      }
    }
  }

  private def encodeParameters(actionParameters: Map[String, String]): String = {
    actionParameters.toSeq.map {
      case (key, value) =>
        URLEncoder.encode(key, StandardCharsets.UTF_8.toString) +
          "=" +
          URLEncoder.encode(value, StandardCharsets.UTF_8.toString)
    }.mkString("&")
  }
}
