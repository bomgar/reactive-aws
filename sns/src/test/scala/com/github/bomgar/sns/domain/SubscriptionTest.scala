package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification

class SubscriptionTest extends Specification {
  "A Subsription" should {

    "parse create topic result with a confirmed subscription" in {
      val createSubscriptionXml =
        <SubscribeResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <SubscribeResult>
            <SubscriptionArn>arn:aws:sns:us-west-2:123456789012:MyTopic:6b0e71bd-7e97-4d97-80ce-4a0994e55286</SubscriptionArn>
          </SubscribeResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </SubscribeResponse>

      val subscription = Subscription.fromSubscribeResult(createSubscriptionXml)

      subscription.subscriptionArn must beSome ("arn:aws:sns:us-west-2:123456789012:MyTopic:6b0e71bd-7e97-4d97-80ce-4a0994e55286")
      subscription.confirmed must beTrue
    }

    "parse create topic result with an unconfirmed subscription" in {
      val createSubscriptionXml =
        <SubscribeResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <SubscribeResult>
            <SubscriptionArn>pending confirmation</SubscriptionArn>
          </SubscribeResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </SubscribeResponse>

      val subscription = Subscription.fromSubscribeResult(createSubscriptionXml)

      subscription.subscriptionArn must beNone
      subscription.confirmed must beFalse
    }

    "parse a list of subscriptions by topic" in {
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
            <NextToken>AAFOcH5cJ233ZKyqgLfr+RuVkwDUan2MwyoF0yAP6J64zg==</NextToken>
          </ListSubscriptionsByTopicResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </ListSubscriptionsByTopicResponse>

      val subscriptions = Subscription.fromListSubscriptionsResult(listSubscriptionsByTopic)

      subscriptions.length must beEqualTo(2)
      subscriptions(0).subscriptionArn must beSome("arn:aws:sns:eu-central-1:dufte:truppe:2af7019d-1ac0-47e6-ffff-2382c49fcbdd")
      subscriptions(0).confirmed must beTrue
      subscriptions(1).subscriptionArn must beNone
      subscriptions(1).confirmed must beFalse
    }

    "parse a list of all subscriptions" in {
      val listSubscriptions =
        <ListSubscriptionsResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <ListSubscriptionsResult>
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
            <NextToken>AAFOcH5cJ233ZKyqgLfr+RuVkwDUan2MwyoF0yAP6J64zg==</NextToken>
          </ListSubscriptionsResult>
          <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
          </ResponseMetadata>
        </ListSubscriptionsResponse>

      val subscriptions = Subscription.fromListSubscriptionsResult(listSubscriptions)

      subscriptions.length must beEqualTo(2)
      subscriptions(0).subscriptionArn must beSome("arn:aws:sns:eu-central-1:dufte:truppe:2af7019d-1ac0-47e6-ffff-2382c49fcbdd")
      subscriptions(0).confirmed must beTrue
      subscriptions(1).subscriptionArn must beNone
      subscriptions(1).confirmed must beFalse
    }
  }
}
