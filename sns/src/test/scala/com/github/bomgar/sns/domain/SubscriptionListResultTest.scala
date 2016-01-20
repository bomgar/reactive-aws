package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification

class SubscriptionListResultTest extends Specification {

  "parse a list of all subscriptions" in {
    val subscriptionListXml =
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

    val subscriptionListResult = SubscriptionListResult.fromSubscriptionListResult(subscriptionListXml)

    subscriptionListResult.nextPageToken must beSome ("AAFOcH5cJ233ZKyqgLfr+RuVkwDUan2MwyoF0yAP6J64zg==")
    val subscriptions = subscriptionListResult.subscriptions
    subscriptions.length must beEqualTo(2)
    subscriptions(0).subscriptionArn must beSome("arn:aws:sns:eu-central-1:dufte:truppe:2af7019d-1ac0-47e6-ffff-2382c49fcbdd")
    subscriptions(0).confirmed must beTrue
    subscriptions(1).subscriptionArn must beNone
    subscriptions(1).confirmed must beFalse
  }
}
