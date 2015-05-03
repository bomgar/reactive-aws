package com.github.bomgar.sqs.domain

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import com.github.bomgar.sqs.AwsSqsClient
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.specification.mutable.SpecificationFeatures
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object IntegrationTestStarter extends App with SpecificationFeatures {

  val accessKey = args(0)
  val secretKey = args(1)
  val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  val region = Region.EU_WEST_1

  println(awsCredentials)

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  testListQueues()
  testGetQueue()

  wsClient.close()


  private def testGetQueue(): Unit = {
    val testQueue = Await.result(client.getQueueByName("test-queue"), 2.seconds)
    println(testQueue)
    testQueue.url must contain("test-queue")
  }

  private def testListQueues(): Unit = {
    val queues = Await.result(client.listQueues(), 2.seconds)
    println(queues)
    queues must have size 1
  }
}
