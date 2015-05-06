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
import scala.util.Random

object IntegrationTestStarter extends App with SpecificationFeatures with FutureAwaits with DefaultAwaitTimeout {

  val accessKey = args(0)
  val secretKey = args(1)

  val queueName: String = Random.alphanumeric.take(10).mkString

  val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  val region = Region.EU_WEST_1

  println(awsCredentials)

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  val queue = testCreateQueue()
  testListQueues()
  testGetQueue()
  testSendMessage()
  testDeleteQueue()

  wsClient.close()

  private def testSendMessage(): Unit = {
    val writer = client.newWriterForQueue(queue)
    val message = await(writer.sendMessage("test"))
    message.md5OfMessageBody must be equalTo "098f6bcd4621d373cade4e832627b4f6"
  }

  private def testCreateQueue(): QueueReference = {
    val testQueue = await(client.createQueue(queueName))
    println(testQueue)
    testQueue.url must contain(queueName)
    testQueue
  }

  private def testDeleteQueue(): Unit = {
    await(client.deleteQueue(queue))
  }

  private def testGetQueue(): Unit = {
    val testQueue = await(client.getQueueByName(queueName))
    println(testQueue)
    testQueue.url must contain(queueName)
  }

  private def testListQueues(): Unit = {
    val queues = await(client.listQueues())
    println(queues)
    queues.length must be greaterThanOrEqualTo 1
  }
}
