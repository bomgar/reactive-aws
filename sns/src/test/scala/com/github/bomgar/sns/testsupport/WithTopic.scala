package com.github.bomgar.sns.testsupport

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import com.github.bomgar.sns.AwsSnsClient
import org.specs2.mutable.After
import org.specs2.specification.Scope
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class WithTopic(wsClient: WSClient) extends Scope with FutureAwaits with DefaultAwaitTimeout with After {

  lazy val accessKey: String = Option(System.getenv("SNS_AWS_ACCESS_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SNS_AWS_ACCESS_KEY"))
  lazy val secretKey: String = Option(System.getenv("SNS_AWS_SECRET_KEY")).getOrElse(throw new IllegalArgumentException("Missing variable SNS_AWS_SECRET_KEY"))

  lazy val awsCredentials = new BasicAwsCredentials(awsAccessKeyId = accessKey, awsSecretKey = secretKey)
  lazy val region = Region.EU_CENTRAL_1

  lazy val client = new AwsSnsClient(new BasicAwsCredentialsProvider(awsCredentials), region, wsClient: WSClient)

  lazy val topicName : String = generateRandomName()
  lazy val testTopic = await(client.createTopic(topicName))

  def generateRandomName(): String = Random.alphanumeric.take(10).mkString

  override def after: Any = {
    // await(client.deleteTopic(testTopic))
  }
}
