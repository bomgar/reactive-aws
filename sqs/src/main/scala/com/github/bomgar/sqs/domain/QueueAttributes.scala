package com.github.bomgar.sqs.domain

import java.time.{Instant, Duration}

import scala.xml.Elem
import QueueAttributes._

case class QueueAttributes(attributes: Map[String, String]) {
  def approximateNumberOfMessages: Option[Long] = attributes.get(ApproximateNumberOfMessages).map(_.toLong)
  def approximateNumberOfMessagesNotVisible: Option[Long] = attributes.get(ApproximateNumberOfMessagesNotVisible).map(_.toLong)
  def visibilityTimeout: Option[Duration] = attributes.get(VisibilityTimeout).map(_.toLong).map(Duration.ofSeconds)
  def lastModifiedTimestamp : Option[Instant] = attributes.get(LastModifiedTimestamp).map(_.toLong).map(Instant.ofEpochSecond)
  def policy: Option[String] = attributes.get(Policy)
  def maximumMessageSize : Option[Long] = attributes.get(MaximumMessageSize).map(_.toLong)
  def messageRetentionPeriod: Option[Duration] = attributes.get(MessageRetentionPeriod).map(_.toLong).map(Duration.ofSeconds)
  def queueArn : Option[String] = attributes.get(QueueArn)
  def approximateNumberOfMessagesDelayed: Option[Long] = attributes.get(ApproximateNumberOfMessagesDelayed).map(_.toLong)
  def delay : Option[Duration] = attributes.get(DelaySeconds).map(_.toLong).map(Duration.ofSeconds)
  def receiveMessageWaitTime: Option[Duration] = attributes.get(ReceiveMessageWaitTimeSeconds).map(_.toLong).map(Duration.ofSeconds)
  def redrivePolicy: Option[String] = attributes.get(RedrivePolicy)
}

object QueueAttributes {

  val All = "All"
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