package com.github.bomgar.sns.testsupport

import com.github.bomgar.auth.credentials.BasicAwsCredentialsProvider
import com.github.bomgar.sqs.AwsSqsClient
import play.api.libs.ws.WSClient
import scala.concurrent.ExecutionContext.Implicits.global

class WithTopicAndTestQueue (wsClient: WSClient) extends WithTopic (wsClient) {

  lazy val sqsClient = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials),region, wsClient)
  lazy val queueName : String = generateRandomName()
  lazy val testQueue = await(sqsClient.createQueue(queueName))
  lazy val testQueueAttributes = await(sqsClient.getQueueAttributes(testQueue,Seq("QueueArn")))
  lazy val testQueueArn: String = testQueueAttributes.queueArn.getOrElse(throw new RuntimeException("Failed to get arn from testqueue"))

}
