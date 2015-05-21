package com.github.bomgar.sns

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.github.bomgar.client.BaseAwsClient
import com.github.bomgar.sns.domain.TopicReference
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

  def createTopic(topicName:String): Future[TopicReference] = {
    val actionParameters = Map(
      "Action" -> "CreateTopic",
      "Name" -> topicName
    )

    executeFormEncodedAction(actionParameters)
      .map(TopicReference.fromCreateTopicResult)
  }

}
