package com.github.bomgar.sns

import com.github.bomgar.sns.domain.{TopicPermission, SubscriptionReference}
import com.github.bomgar.sns.testsupport.{WithTopicAndTestQueue, WithTopic}
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.concurrent.ExecutionContext.Implicits.global

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
    "set attribute for existing topic" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)
      val displayName: String = "dufte"

      await(client.setTopicAttribute(testTopic, "DisplayName", displayName))

      val topicAttributes = await(client.getTopicAttributes(testTopic))
      topicAttributes.displayName must beSome (displayName)
    }

    tag("integration")
    "publish a message" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      //amazon needs some time to include it in the list
      Thread.sleep(2000)

      await(client.publish("TestMessage",testTopic))
    }

    tag("integration")
    "subscribe to a topic" in new WithTopicAndTestQueue(wsClient) {
      testTopic // create instance of lazy val
      Thread.sleep(2000)
      testQueueArn
      val subscriptionReference = await(client.subscribe(testTopic, testQueueArn, "sqs" ))

      subscriptionReference.confirmed must beTrue
      subscriptionReference.subscriptionArn must not beNone

      Thread.sleep(40000) // 13 Jan 2016: Subscription assigned reliable not before 60sec
      val topicAttributes = await(client.getTopicAttributes(testTopic))

      topicAttributes.subscriptionsConfirmed must beSome (1)
    }

    tag("integration")
    "list subscriptions by topic" in new WithTopicAndTestQueue(wsClient) {
      testTopic // create instance of lazy val
      Thread.sleep(2000)
      testQueueArn
      await(client.subscribe(testTopic, testQueueArn, "sqs" ))
      await(client.subscribe(testTopic, "success@simulator.amazonses.com", "email"))

      val topicSubscription = await(client.listSubscriptionsByTopics(testTopic))

      topicSubscription.length must beEqualTo(2)

      topicSubscription must contain(SubscriptionReference.fromSubscriptionArn(testQueueArn))
      topicSubscription must contain(SubscriptionReference.fromSubscriptionArn("confirmation pending"))
    }

    tag("integration")
    "set permission for topic" in new WithTopic(wsClient) {
      testTopic // create instance of lazy val
      Thread.sleep(2000)
      val awsId = Option(System.getenv("AWS_ID")).getOrElse(throw new IllegalArgumentException("Missing variable AWS_ID"))
      val permission = new TopicPermission(
        testTopic,
        "TestPermission",
        List("Publish"),
        List(awsId))

      await(client.addPermission(permission))
    }

  }

  override def afterAll() = wsClient.close()

}
