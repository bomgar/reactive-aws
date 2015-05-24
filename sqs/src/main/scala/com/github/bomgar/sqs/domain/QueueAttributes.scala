package com.github.bomgar.sqs.domain

import scala.xml.Elem
import QueueAttributes._

case class QueueAttributes(attributes: Map[String, String]) {
  def approximateNumberOfMessages: Option[String] = attributes.get(ApproximateNumberOfMessages)
  def approximateNumberOfMessagesNotVisible: Option[String] = attributes.get(ApproximateNumberOfMessagesNotVisible)
  def visibilityTimeout: Option[String] = attributes.get(VisibilityTimeout)
  def lastModifiedTimestamp : Option[String] = attributes.get(LastModifiedTimestamp)
  def policy: Option[String] = attributes.get(Policy)
  def maximumMessageSize : Option[String] = attributes.get(MaximumMessageSize)
  def messageRetentionPeriod: Option[String] = attributes.get(MessageRetentionPeriod)
  def queueArn : Option[String] = attributes.get(QueueArn)
  def approximateNumberOfMessagesDelayed: Option[String] = attributes.get(ApproximateNumberOfMessagesDelayed)
  def delaySeconds : Option[String] = attributes.get(DelaySeconds)
  def receiveMessageWaitTimeSeconds: Option[String] = attributes.get(ReceiveMessageWaitTimeSeconds)
  def redrivePolicy: Option[String] = attributes.get(RedrivePolicy)
}

object QueueAttributes {

  val RedrivePolicy = "RedrivePolicy"
  val ReceiveMessageWaitTimeSeconds = "ReceiveMessageWaitTimeSeconds"
  val DelaySeconds = "DelaySeconds"
  val ApproximateNumberOfMessagesDelayed = "ApproximateNumberOfMessagesDelayed"
  val QueueArn = "QueueArn"
  val MessageRetentionPeriod = "MessageRetentionPeriod"
  val MaximumMessageSize = "MaximumMessageSize"
  val Policy = "Policy"
  val LastModifiedTimestamp = "LastModifiedTimestamp"
  val VisibilityTimeout = "VisibilityTimeout"
  val ApproximateNumberOfMessagesNotVisible = "ApproximateNumberOfMessagesNotVisible"
  val ApproximateNumberOfMessages = "ApproximateNumberOfMessages"


  def fromGetQueueAttributesResponse(response: Elem): QueueAttributes = {
    val attributes = (response \\ "Attribute").map{attribute =>
      val name = (attribute \ "Name").text
      val value = (attribute \ "Value").text
      name -> value
    }.toMap
    new QueueAttributes(attributes)
  }
}