package com.github.bomgar.sqs

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.github.bomgar.client.BaseAwsClient
import com.github.bomgar.sqs.domain.{QueueAttributes, Message, MessageReference, QueueReference}
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class AwsSqsClient(
                    credentialsProvider: AwsCredentialsProvider,
                    region: Region.Type, client: WSClient,
                    defaultTimeout: Duration = 5.seconds
                    )(implicit executionContext: ExecutionContext)
  extends BaseAwsClient(credentialsProvider, region, client, "sqs", defaultTimeout) {

  def listQueues(): Future[Seq[QueueReference]] = {
    val actionParameters = Map(
      "Action" -> "ListQueues",
      "Version" -> "2012-11-05"
    )

    executeFormEncodedAction(actionParameters)
      .map(QueueReference.fromListQueueResult)
  }

  def getQueueByName(queueName: String): Future[QueueReference] = {
    val actionParameters = Map(
      "Action" -> "GetQueueUrl",
      "Version" -> "2012-11-05",
      "QueueName" -> queueName
    )

    executeFormEncodedAction(actionParameters)
      .map(QueueReference.fromQueueUrlResult)

  }

  def createQueue(queueName: String): Future[QueueReference] = {
    val actionParameters = Map(
      "Action" -> "CreateQueue",
      "Version" -> "2012-11-05",
      "QueueName" -> queueName
    )

    executeFormEncodedAction(actionParameters)
      .map(QueueReference.fromCreateQueueResult)

  }

  def deleteQueue(queue: QueueReference): Future[Unit] = {
    val actionParameters = Map(
      "Action" -> "DeleteQueue",
      "Version" -> "2012-11-05"
    )
    executeFormEncodedAction(actionParameters, queue.url).map(_ => ())
  }

  def purgeQueue(queue: QueueReference): Future[Unit] = {
    val actionParameters = Map(
      "Action" -> "PurgeQueue",
      "Version" -> "2012-11-05"
    )
    executeFormEncodedAction(actionParameters, queue.url).map(_ => ())
  }

  private[sqs] def sendMessage(queue: QueueReference, message: String): Future[MessageReference] = {
    val actionParameters = Map(
      "Action" -> "SendMessage",
      "Version" -> "2012-11-05",
      "MessageBody" -> message
    )

    executeFormEncodedAction(actionParameters, queue.url)
      .map(MessageReference.fromSendMessageResponse)

  }

  def newWriterForQueue(queue: QueueReference) = new QueueWriter(this, queue)

  private[sqs] def receiveMessages(queue: QueueReference, maxNumberOfMessages: Int = 1, waitTimeInSeconds: Option[Int] = None): Future[Seq[Message]] = {
    val actionParameters = Map(
      "Action" -> "ReceiveMessage",
      "Version" -> "2012-11-05",
      "MaxNumberOfMessages" -> maxNumberOfMessages.toString
    )

    val actionParametersWithWaitTime = waitTimeInSeconds.fold(actionParameters)(seconds => actionParameters + ("WaitTimeSeconds" -> seconds.toString))

    val timeout = waitTimeInSeconds.fold(defaultTimeout)(seconds => defaultTimeout + Duration(seconds, SECONDS))

    executeFormEncodedAction(actionParametersWithWaitTime, queue.url, timeout)
      .map(Message.fromReceiveMessageResult)
  }

  private[sqs] def getQueueAttributes(queue: QueueReference, attributes: Seq[String]): Future[QueueAttributes] = {
    val actionParameters = Map(
      "Action" -> "GetQueueAttributes",
      "Version" -> "2012-11-05"
    )
    val parametersWithAttributes = attributes.zipWithIndex.foldLeft(actionParameters) { (parameters, attribute) =>
      val (attributeName, index) = attribute
      parameters + (s"AttributeName.${index+1}" -> attributeName)
    }

    executeFormEncodedAction(parametersWithAttributes, queue.url)
      .map(QueueAttributes.fromGetQueueAttributesResponse)

  }

  def newReaderForQueue(queue: QueueReference) = new QueueReader(this, queue)

  def deleteMessage(queue: QueueReference, receiptHandle: String): Future[Unit] = {
    val actionParameters = Map(
      "Action" -> "DeleteMessage",
      "Version" -> "2012-11-05",
      "ReceiptHandle" -> receiptHandle
    )
    executeFormEncodedAction(actionParameters, queue.url).map(_ => ())
  }
}
