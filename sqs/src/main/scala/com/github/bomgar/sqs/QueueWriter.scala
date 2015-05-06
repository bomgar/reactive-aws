package com.github.bomgar.sqs

import com.github.bomgar.sqs.domain.{MessageReference, QueueReference}
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.Future


class QueueWriter private[sqs](sqsClient: AwsSqsClient, queueReference: QueueReference) {

  @inline
  def sendMessage(message: String): Future[MessageReference] = {
    sqsClient.sendMessage(queueReference, message)
  }

  @inline
  def sendMessage(message: JsValue): Future[MessageReference] = {
    sendMessage(Json.stringify(message))
  }

  def sendMessage[T](message: T)(implicit writes: Writes[T]): Future[MessageReference] = {
    sendMessage(Json.toJson(message))
  }
}
