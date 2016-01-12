package com.github.bomgar.sns

import com.github.bomgar.sns.domain.TopicReference
import com.github.bomgar.sns.testsupport.WithTopic
import com.ning.http.client.AsyncHttpClientConfig.Builder
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll
import play.api.libs.ws.ning.NingWSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

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

      val topicAttributes = await(client.getTopicAttributes(testTopic.topicArn))

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
      //amazon needs some time to include it in the list
      Thread.sleep(2000)

      val subscriptionReference = await(client.subscribe(testTopic, "mathias.muenscher@kreuzwerker.de", "email" ))

      subscriptionReference.confirmed must beFalse
      subscriptionReference.subscriptionArn must beNone
    }

  }

  override def afterAll() = wsClient.close()

}
