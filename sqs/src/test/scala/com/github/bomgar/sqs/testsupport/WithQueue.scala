package com.github.bomgar.sqs.testsupport

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentialsProvider, BasicAwsCredentials}
import com.github.bomgar.sqs.AwsSqsClient
import org.specs2.mutable.After
import org.specs2.specification.Scope
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Random

class WithQueue(wsClient: WSClient) extends Scope with FutureAwaits with DefaultAwaitTimeout with After {

  lazy val accessKey: String = Option(System.getenv("SQS_AWS_ACCESS_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SQS_AWS_ACCESS_KEY"))
  lazy val secretKey: String = Option(System.getenv("SQS_AWS_SECRET_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SQS_AWS_SECRET_KEY"))

  lazy val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  lazy val region = Region.EU_WEST_1

  lazy val client = new AwsSqsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  lazy val queueName: String = aQueueName()
  lazy val testQueue = await(client.createQueue(queueName))

  private def aQueueName(): String = Random.alphanumeric.take(10).mkString

  override def after: Any = {
    await(client.deleteQueue(testQueue))
  }
}
