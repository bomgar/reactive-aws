package com.github.bomgar.sns.domain

import scala.xml.Elem

case class SubscriptionReference(subscriptionArn: Option[String], confirmed: Boolean)

object SubscriptionReference {

  def fromSubscribeResult(subscribeResult: Elem): SubscriptionReference = {

    val subscriptionArnReturnValue = (subscribeResult \\ "SubscriptionArn").map(_.text).head
    val confirmed = subscriptionArnReturnValue.startsWith("arn:aws:sns")

    val subscriptionArn = if (confirmed) {
      Option(subscriptionArnReturnValue)
    } else {
      None
    }

    new SubscriptionReference(subscriptionArn, confirmed)
  }

}
