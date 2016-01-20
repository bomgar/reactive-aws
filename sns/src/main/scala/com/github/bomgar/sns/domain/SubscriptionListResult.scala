package com.github.bomgar.sns.domain

import scala.xml.{Node, Elem}

case class SubscriptionListResult(
                          override val nextPageToken: Option[String],
                          subscriptions: Seq[Subscription]
                        ) extends PagedResult {}

object SubscriptionListResult {

  def fromSubscriptionListResult(listSubscriptionsResult: Elem): SubscriptionListResult = new SubscriptionListResult(
    (listSubscriptionsResult \\ "NextToken").map(_.text).headOption,
    Subscription.fromListSubscriptionsResult(listSubscriptionsResult)
  )

}
