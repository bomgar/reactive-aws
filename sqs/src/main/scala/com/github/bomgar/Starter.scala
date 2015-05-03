package com.github.bomgar

import com.github.bomgar.auth.credentials.{BasicAwsCredentialsProvider, BasicAwsCredentials}
import com.github.bomgar.sqs.AwsSqsClient
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Starter extends App {

  val accessKey = args(0)
  val secretKey = args(1)
  val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  val region = Region.EU_WEST_1

  println(awsCredentials)

  val ningConfig = new AsyncHttpClientConfig.Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)
  println(Await.result(client.listQueues(), 2.seconds))
  println(Await.result(client.getQueueByName("test-queue"), 2.seconds))

  wsClient.close()
}
