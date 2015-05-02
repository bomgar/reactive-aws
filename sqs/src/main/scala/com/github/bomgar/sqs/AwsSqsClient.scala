package com.github.bomgar.sqs

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.github.bomgar.client.BaseAwsClient
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext


class AwsSqsClient(credentialsProvider: AwsCredentialsProvider, region: Region.Type, client: WSClient)(implicit executionContext: ExecutionContext)
  extends BaseAwsClient(credentialsProvider, region, client, "sqs") {



  def listQueues() = {
    val response = client.url(baseUrl)
      .withHeaders(
        "Content-Type" -> "application/x-www-form-urlencoded"
      )
      .sign(signer)
      .post("Action=ListQueues&Version=2012-11-05")

    response.map{
      response =>
        response.xml
    }
  }
}
