package com.github.bomgar.sqs

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.github.bomgar.client.BaseAwsClient
import com.github.bomgar.sqs.domain.QueueReference
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}


class AwsSqsClient(credentialsProvider: AwsCredentialsProvider, region: Region.Type, client: WSClient)(implicit executionContext: ExecutionContext)
  extends BaseAwsClient(credentialsProvider, region, client, "sqs") {

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
}
