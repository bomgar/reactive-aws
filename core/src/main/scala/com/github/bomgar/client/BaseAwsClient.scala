package com.github.bomgar.client

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.github.bomgar.Region
import com.github.bomgar.auth.AWS4SignerForAuthorizationHeader
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import org.slf4j.LoggerFactory
import play.api.http.Status._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Elem

class BaseAwsClient(
                     val credentialsProvider: AwsCredentialsProvider,
                     val region: Region.Type,
                     val client: WSClient,
                     val serviceName: String
                     )(implicit executionContext: ExecutionContext) {

  val log = LoggerFactory.getLogger(getClass)

  val baseUrl = s"https://$serviceName.$region.amazonaws.com"

  val signer = new AWS4SignerForAuthorizationHeader(credentialsProvider, region, serviceName)

  protected def executeFormEncodedAction(actionParameters: Map[String, String]): Future[Elem] = {
    val body: String = encodeParameters(actionParameters)

    val response = client
      .url(baseUrl)
      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .sign(signer)
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
