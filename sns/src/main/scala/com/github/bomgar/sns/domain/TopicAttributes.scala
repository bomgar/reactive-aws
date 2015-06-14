package com.github.bomgar.sns.domain

import scala.xml.Elem
import TopicAttributes._

case class TopicAttributes (attributes: Map[String, String]) {
  def subscriptionsConfirmed: Option[Long] = attributes.get(SubscriptionsConfirmed).map(_.toLong)

}

object TopicAttributes {

  val SubscriptionsConfirmed = "SubscriptionsConfirmed"

  def fromGetQueueAttributesResponse(response: Elem): TopicAttributes = {
    val attributes = (response \\ "entry").map{attribute =>
      val key = (attribute \ "key").text
      val value = (attribute \ "value").text
      key -> value
    }.toMap
    new TopicAttributes(attributes)
  }
}



