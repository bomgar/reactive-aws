package com.github.bomgar.sqs

import com.github.bomgar.sqs.domain.QueueAttributes
import com.github.bomgar.sqs.testsupport.WithQueue
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class SqsIntegrationTest extends Specification with FutureAwaits with DefaultAwaitTimeout with AfterAll {

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  "A sqs client" should {

    tag("integration")
    "send messages" in new WithQueue(wsClient) {
      val messageBody: String = "test"
      val writer = client.newWriterForQueue(testQueue)
      val message = await(writer.sendMessage(messageBody))
      message.md5OfMessageBody must be equalTo "098f6bcd4621d373cade4e832627b4f6"
    }

    tag("integration")
    "receive messages" in new WithQueue(wsClient) {
      val messageBody: String = "test"
      val writer = client.newWriterForQueue(testQueue)
      await(writer.sendMessage(messageBody))

      val reader = client.newReaderForQueue(testQueue)
      val message = await(reader.longPollSingleMessage(10)).get
      await(reader.acknowledgeMessage(message))

      message.body must be equalTo messageBody
    }

    tag("integration")
    "get queues" in new WithQueue(wsClient) {
      val queue = testQueue
      val queueByName = await(client.getQueueByName(queueName))
      queueByName.url must contain(queueName)
      queueByName must be equalTo queue
    }

    tag("integration")
    "list queues" in new WithQueue(wsClient) {
      val queue = testQueue
      //amazon needs some time to include it in the list
      Thread.sleep(2000)
      val queues = await(client.listQueues())
      queues.length must be greaterThanOrEqualTo 1
    }

    tag("integration")
    "get queue attributes" in new WithQueue(wsClient) {
      val queue = testQueue
      await(client.purgeQueue(queue))
      val attributes = await(client.getQueueAttributes(queue, Seq(QueueAttributes.ApproximateNumberOfMessages)))
      attributes.approximateNumberOfMessages must beSome("0")

    }
  }

  override def afterAll() = wsClient.close()

}
