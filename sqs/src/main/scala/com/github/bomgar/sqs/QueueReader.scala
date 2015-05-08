package com.github.bomgar.sqs

import com.github.bomgar.sqs.domain.{Message, QueueReference}

import scala.concurrent.{ExecutionContext, Future}

class QueueReader private[sqs](sqsClient: AwsSqsClient, queueReference: QueueReference)(implicit executionContext: ExecutionContext) {

  def receiveSingleMessage: Future[Option[Message]] =
    receiveMessages(maxNumberOfMessages = 1).map(_.headOption)

  def longPollSingleMessage(waitTimeInSeconds: Int): Future[Option[Message]] =
    longPollMessages(maxNumberOfMessages = 1, waitTimeInSeconds = waitTimeInSeconds).map(_.headOption)

  def receiveMessages(maxNumberOfMessages: Int): Future[Seq[Message]] =
    sqsClient.receiveMessages(queueReference, maxNumberOfMessages = 1)

  def longPollMessages(maxNumberOfMessages: Int, waitTimeInSeconds: Int): Future[Option[Message]] =
    sqsClient.receiveMessages(queueReference, maxNumberOfMessages = 1, waitTimeInSeconds = Some(waitTimeInSeconds)).map(_.headOption)

}