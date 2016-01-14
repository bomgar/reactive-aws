package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification

class SubscriptionReferenceTest extends Specification {
  "A SubsriptionReference" should {

    "parse create topic result with a confirmed subscription" in {
      val createSubscriptionResult =
        <SubscribeResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <SubscribeResult>
            <SubscriptionArn>arn:aws:sns:us-west-2:123456789012:MyTopic:6b0e71bd-7e97-4d97-80ce-4a0994e55286</SubscriptionArn>
          </SubscribeResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </SubscribeResponse>

      val subscriptionReference = SubscriptionReference.fromSubscribeResult(createSubscriptionResult)

      subscriptionReference.subscriptionArn must beSome ("arn:aws:sns:us-west-2:123456789012:MyTopic:6b0e71bd-7e97-4d97-80ce-4a0994e55286")
      subscriptionReference.confirmed must beTrue
    }

    "parse create topic result with an unconfirmed subscription" in {
      val createSubscriptionResult =
        <SubscribeResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <SubscribeResult>
            <SubscriptionArn>pending confirmation</SubscriptionArn>
          </SubscribeResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </SubscribeResponse>

      val subscriptionReference = SubscriptionReference.fromSubscribeResult(createSubscriptionResult)

      subscriptionReference.subscriptionArn must beNone
      subscriptionReference.confirmed must beFalse
    }

    "parse parse a list of subscriptions" in {
      val listSubscriptionsByTopic =
        <ListSubscriptionsByTopicResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <ListSubscriptionsByTopicResult>
            <Subscriptions>
              <member>
                <Owner>370621384784</Owner>
                <Protocol>sqs</Protocol>
                <Endpoint>arn:aws:sqs:eu-central-1:dufte:truppe</Endpoint>
                <SubscriptionArn>arn:aws:sns:eu-central-1:dufte:truppe:2af7019d-1ac0-47e6-ffff-2382c49fcbdd</SubscriptionArn>
                <TopicArn>arn:aws:sns:eu-central-1:dufte:truppe</TopicArn>
              </member>
              <member>
                <Owner>370621384784</Owner>
                <Protocol>email</Protocol>
                <Endpoint>bla@bla.de</Endpoint>
                <SubscriptionArn>PendingConfirmation</SubscriptionArn>
                <TopicArn>arn:aws:sns:eu-central-1:dufte:truppe</TopicArn>
              </member>
            </Subscriptions>
          </ListSubscriptionsByTopicResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </ListSubscriptionsByTopicResponse>

      val subscriptionReferences = SubscriptionReference.fromListSubscriptionByTopicResult(listSubscriptionsByTopic)

      subscriptionReferences.length must beEqualTo(2)
      subscriptionReferences(0).subscriptionArn must beSome("arn:aws:sns:eu-central-1:dufte:truppe:2af7019d-1ac0-47e6-ffff-2382c49fcbdd")
      subscriptionReferences(0).confirmed must beTrue
      subscriptionReferences(1).subscriptionArn must beNone
      subscriptionReferences(1).confirmed must beFalse
    }
  }
}
