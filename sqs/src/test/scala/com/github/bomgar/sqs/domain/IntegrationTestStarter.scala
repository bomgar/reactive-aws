package com.github.bomgar.sqs.domain

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import com.github.bomgar.sqs.AwsSqsClient
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.specification.mutable.SpecificationFeatures
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

object IntegrationTestStarter extends App with SpecificationFeatures with FutureAwaits with DefaultAwaitTimeout {

  val accessKey = args(0)
  val secretKey = args(1)
  val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  val region = Region.EU_WEST_1

  println(awsCredentials)

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  testCreateQueue()
  testListQueues()
  testGetQueue()

  wsClient.close()


  private def testCreateQueue(): Unit = {
    val testQueue = await(client.createQueue("test-queue"))
    println(testQueue)
    testQueue.url must contain("test-queue")
  }

  private def testGetQueue(): Unit = {
    val testQueue = await(client.getQueueByName("test-queue"))
    println(testQueue)
    testQueue.url must contain("test-queue")
  }

  private def testListQueues(): Unit = {
    val queues = await(client.listQueues())
    println(queues)
    queues must have size 1
  }
}
