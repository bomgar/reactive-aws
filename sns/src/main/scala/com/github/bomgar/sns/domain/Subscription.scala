package com.github.bomgar.sns.domain

import scala.xml.{Node, Elem}

case class Subscription(
                         subscriptionArn: Option[String],
                         topicArn: Option[String],
                         endpoint: Option[String],
                         protocol: Option[String],
                         owner: Option[String],
                         confirmed: Boolean)

object Subscription {

  def fromSubscribeResult(subscribeResult: Elem): Subscription = {

    val subscriptionArnResult = (subscribeResult \\ "SubscriptionArn").map(_.text).head
    val subscriptionArn = this.subscriptionArnFromResult(subscriptionArnResult)

    new Subscription(
      subscriptionArn,
      topicArn = None,
      endpoint = None,
      protocol = None,
      owner = None,
      confirmed = subscriptionArn.isDefined
    )

  }

  def fromSubscriptionListNode(subscriptionNode: Node): Subscription = {
    val subscriptionArnResult = (subscriptionNode \\ "SubscriptionArn").map(_.text).head
    val topicArn = (subscriptionNode \\ "TopicArn").map(_.text).head
    val protocol = (subscriptionNode \\ "Protocol").map(_.text).head
    val owner = (subscriptionNode \\ "Owner").map(_.text).head
    val endpoint = (subscriptionNode \\ "Endpoint").map(_.text).head

    val subscriptionArn = this.subscriptionArnFromResult(subscriptionArnResult)

    new Subscription(
      subscriptionArn,
      Option(topicArn),
      Option(endpoint),
      Option(protocol),
      Option(owner),
      confirmed = subscriptionArn.isDefined
    )
  }

  def fromListSubscriptionsResult(listSubscriptionsResult: Elem): Seq[Subscription] = {
    (listSubscriptionsResult \\ "member").map(node => fromSubscriptionListNode(node))
  }

  def subscriptionArnFromResult(subscriptionArn: String) : Option[String] = {
    if (subscriptionArn.startsWith("arn:aws:sns")) {
      Option(subscriptionArn)
    } else {
      None
    }
  }

}
