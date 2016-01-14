package com.github.bomgar.sns.domain

import scala.xml.Elem

case class SubscriptionReference(subscriptionArn: Option[String], confirmed: Boolean)

object SubscriptionReference {

  def fromSubscribeResult(subscribeResult: Elem): SubscriptionReference = {

    val subscriptionArn = (subscribeResult \\ "SubscriptionArn").map(_.text).head

    fromSubscriptionArn(subscriptionArn)
  }

  def fromListSubscriptionByTopicResult(listSubscriptionsResult: Elem): Seq[SubscriptionReference] = {
    (listSubscriptionsResult \\ "SubscriptionArn").map(arnNode => fromSubscriptionArn(arnNode.text))
  }

  def fromSubscriptionArn(subscriptionArn: String): SubscriptionReference = {
    val confirmed = subscriptionArn.startsWith("arn:aws:sns")

    val arn = if (confirmed) {
      Option(subscriptionArn)
    } else {
      None
    }

    new SubscriptionReference(arn, confirmed)
  }

}
