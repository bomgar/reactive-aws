package com.github.bomgar.client

import com.github.bomgar.Region
import com.github.bomgar.auth.AWS4SignerForAuthorizationHeader
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import org.slf4j.LoggerFactory
import play.api.libs.ws.WSClient

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
    val body = actionParameters.toSeq.map {
      case (key, value) => s"$key=$value"
    }.mkString("&")

    val response = client
      .url(baseUrl)
      .withHeaders(
        "Content-Type" -> "application/x-www-form-urlencoded"
      )
      .sign(signer)
      .post(body)

    response.map { response =>
      log.debug("AWS response: {}", response.body)
      response.xml
    }

  }
}
