package com.github.bomgar.sqs

import com.github.bomgar.sqs.domain.{Message, QueueReference}

import scala.concurrent.{ExecutionContext, Future}

/**
 * maxNumberOfMessages
 *
 * The maximum number of messages to return. Amazon SQS never returns
 * more messages than this value but may return fewer. Values can be from
 * 1 to 10. Default is 1. All of the messages are not necessarily
 * returned.
 *
 * waitTimeInSeconds
 *
 * The duration (in seconds) for which the call will wait for a message
 * to arrive in the queue before returning. If a message is available,
 * the call will return sooner than WaitTimeSeconds.
 *
 */
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
