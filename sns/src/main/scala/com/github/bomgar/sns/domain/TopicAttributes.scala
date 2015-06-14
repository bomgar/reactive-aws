package com.github.bomgar.sns.domain

import play.api.libs.json._ //{JsValue, Json}

import scala.xml.Elem
import TopicAttributes._

case class TopicAttributes (attributes: Map[String, String]) {
  def topicArn: Option [String] = attributes.get(TopicArn)
  def owner: Option [Long] = attributes.get(Owner).map(_.toLong)
  def policy: Option [JsValue] = attributes.get(Policy).map(Json.parse(_))
  def displayName: Option [String] = attributes.get(DisplayName)
  def subscriptionsPending: Option [Long] = attributes.get(SubscriptionsPending).map(_.toLong)
  def subscriptionsConfirmed: Option[Long] = attributes.get(SubscriptionsConfirmed).map(_.toLong)
  def subscriptionsDeleted: Option [Long] = attributes.get(SubscriptionsDeleted).map(_.toLong)
  def deliveryPolicy: Option [JsValue] = attributes.get(DeliveryPolicy).map(Json.parse(_))
  def effectiveDeliveryPolicy: Option [JsValue] = attributes.get(EffectiveDeliveryPolicy).map(Json.parse(_))
}

object TopicAttributes {

  val TopicArn = "TopicArn"
  val Owner = "Owner"
  val Policy = "Policy"
  val DisplayName = "DisplayName"
  val SubscriptionsPending = "SubscriptionsPending"
  val SubscriptionsConfirmed = "SubscriptionsConfirmed"
  val SubscriptionsDeleted = "SubscriptionsDeleted"
  val DeliveryPolicy = "DeliveryPolicy"
  val EffectiveDeliveryPolicy = "EffectiveDeliveryPolicy"

  def fromGetTopicAttributesResponse(response: Elem): TopicAttributes = {
    val attributes = (response \\ "entry").map{attribute =>
      val key = (attribute \ "key").text
      val value = (attribute \ "value").text
      key -> value
    }.toMap
    new TopicAttributes(attributes)
  }
}



