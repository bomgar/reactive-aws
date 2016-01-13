package com.github.bomgar.sns

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.github.bomgar.client.BaseAwsClient
import com.github.bomgar.sns.domain.{SubscriptionReference, TopicAttributes, TopicReference}
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class AwsSnsClient(
                    credentialsProvider: AwsCredentialsProvider,
                    region: Region.Type,
                    client: WSClient,
                    defaultTimeout: Duration = 5.seconds
                  )(implicit executionContext: ExecutionContext)
  extends BaseAwsClient(credentialsProvider, region, client, "sns", defaultTimeout) {

  def createTopic(topicName: String): Future[TopicReference] = {
    val actionParameters = Map(
      "Action" -> "CreateTopic",
      "Version" -> "2010-03-31",
      "Name" -> topicName
    )

    executeFormEncodedAction(actionParameters)
      .map(TopicReference.fromCreateTopicResult)
  }

  def getTopicAttributes(topic: TopicReference): Future[TopicAttributes] = {
    val actionParameters = Map(
      "Action" -> "GetTopicAttributes",
      "Version" -> "2010-03-31",
      "TopicArn" -> topic.topicArn
    )

    executeFormEncodedAction(actionParameters)
      .map(TopicAttributes.fromGetTopicAttributesResponse)
  }

  def listTopics(): Future[Seq[TopicReference]] = {
    val actionParameters = Map(
      "Action" -> "ListTopics",
      "Version" -> "2010-03-31"
    )

    executeFormEncodedAction(actionParameters)
      .map(TopicReference.fromListTopicResult)
  }

  def deleteTopic(topic: TopicReference): Future[Unit] = {
    val actionParameters = Map(
      "TopicArn" -> topic.topicArn,
      "Action" -> "DeleteTopic",
      "Version" -> "2010-03-31"
    )
    executeFormEncodedAction(actionParameters).map(_ => ())
  }

  def publish(message: String, topic: TopicReference): Future[Unit] = {
    val actionParameters = Map(
      "TopicArn" -> topic.topicArn,
      "Action" -> "Publish",
      "Message" -> message,
      "Version" -> "2010-03-31"
    )
    executeFormEncodedAction(actionParameters).map(_ => ())
  }

  def subscribe(topic: TopicReference, endpoint: String, protocol: String): Future[SubscriptionReference] = {
    val actionParameters = Map(
      "TopicArn" -> topic.topicArn,
      "Action" -> "Subscribe",
      "Endpoint" -> endpoint,
      "Protocol" -> protocol,
      "Version" -> "2010-03-31"
    )
    executeFormEncodedAction(actionParameters)
      .map(SubscriptionReference.fromSubscribeResult)
  }

}
