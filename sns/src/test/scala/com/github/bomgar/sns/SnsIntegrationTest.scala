package com.github.bomgar.sns

import com.github.bomgar.auth.credentials.BasicAwsCredentialsProvider
import com.github.bomgar.sns.testsupport.WithTopic
import com.github.bomgar.sqs.AwsSqsClient
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.concurrent.ExecutionContext.Implicits.global


import scala.util.Random

class SnsIntegrationTest extends Specification with FutureAwaits with DefaultAwaitTimeout with AfterAll {

  val ningConfig = new Builder().build()
  val wsClient: NingWSClient = new NingWSClient(ningConfig)

  "A sns client" should {

    tag("integration")
    "create a new topic" in new WithTopic(wsClient) {
      testTopic.topicArn must endWith(topicName)
    }

    tag("integration")
    "list existing topics" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)
      val topics = await(client.listTopics())
      topics.length must be greaterThanOrEqualTo 1
    }

    tag("integration")
    "delete existing topics" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)

      await(client.deleteTopic(testTopic))
    }

    tag("integration")
    "get attributes for existing topic" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)

      val topicAttributes = await(client.getTopicAttributes(testTopic))

      topicAttributes.topicArn must beSome (testTopic.topicArn)
    }

    tag("integration")
    "publish a message" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)

      await(client.publish("TestMessage",testTopic))
    }

    tag("integration")
    "subscribe to a topic" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val

      val sqsClient = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials),region, wsClient)
      lazy val queueName: String = Random.alphanumeric.take(10).mkString
      lazy val testQueue = await(sqsClient.createQueue(queueName))

      Thread.sleep(2000)
      val testQueueAttributes = await(sqsClient.getQueueAttributes(testQueue,Seq("QueueArn")))
      val testQueueArn: String = testQueueAttributes.queueArn.getOrElse(throw new RuntimeException("Failed to get arn from testqueue"))
      val subscriptionReference = await(client.subscribe(testTopic, testQueueArn, "sqs" ))

      subscriptionReference.confirmed must beTrue
      subscriptionReference.subscriptionArn must not beNone

      Thread.sleep(60000) // Subscription assigned relliable not before 60sec (13 Jan 2016) - though immediately in mgmt console
      val topicAttributes = await(client.getTopicAttributes(testTopic))

      topicAttributes.subscriptionsConfirmed must beSome (1)
    }

  }

  override def afterAll() = wsClient.close()

}
