package com.github.bomgar.sqs.domain

import com.github.bomgar.sqs.AwsSqsClient
import org.specs2.execute.AsResult
import org.specs2.mutable.{Around, After}
import org.specs2.specification.{AfterAll, SpecificationFeatures, Scope}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.util.Random

class WithQueue(sqsClient: AwsSqsClient) extends Scope with FutureAwaits with DefaultAwaitTimeout with After {

  lazy val queueName: String = aQueueName()
  lazy val testQueue = await(sqsClient.createQueue(queueName))

  private def aQueueName(): String = Random.alphanumeric.take(10).mkString

  override def after: Any = {
    await(sqsClient.deleteQueue(testQueue))
  }
}
