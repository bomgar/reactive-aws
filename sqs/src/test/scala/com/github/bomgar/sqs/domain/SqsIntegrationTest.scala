package com.github.bomgar.sqs.domain

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import com.github.bomgar.sqs.AwsSqsClient
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class SqsIntegrationTest extends Specification with FutureAwaits with DefaultAwaitTimeout with AfterAll {

  val accessKey: String = Option(System.getenv("SQS_AWS_ACCESS_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SQS_AWS_ACCESS_KEY"))
  val secretKey: String = Option(System.getenv("SQS_AWS_SECRET_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SQS_AWS_SECRET_KEY"))

  val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  val region = Region.EU_WEST_1

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  "A sqs client" should {

    tag("integration")
    "send messages" in new WithQueue(client) {
      val messageBody: String = "test"
      val writer = client.newWriterForQueue(testQueue)
      val message = await(writer.sendMessage(messageBody))
      message.md5OfMessageBody must be equalTo "098f6bcd4621d373cade4e832627b4f6"
    }

    tag("integration")
    "receive messages" in new WithQueue(client) {
      val messageBody: String = "test"
      val writer = client.newWriterForQueue(testQueue)
      await(writer.sendMessage(messageBody))

      val reader = client.newReaderForQueue(testQueue)
      val message = await(reader.longPollSingleMessage(10)).get
      await(reader.acknowledgeMessage(message))

      message.body must be equalTo messageBody
    }

    tag("integration")
    "get queues" in new WithQueue(client) {
      val queue = testQueue
      val queueByName = await(client.getQueueByName(queueName))
      queueByName.url must contain(queueName)
      queueByName must be equalTo queue
    }

    tag("integration")
    "list queues" in new WithQueue(client) {
      val queue = testQueue
      //amazon needs some time to include it in the list
      Thread.sleep(2000)
      val queues = await(client.listQueues())
      queues.length must be greaterThanOrEqualTo 1
    }
  }

  override def afterAll() = wsClient.close()

}
