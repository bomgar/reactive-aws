package com.github.bomgar.sqs.domain

import scala.xml.{Node, Elem}

case class Queue(url: String)

object Queue {
  def fromListQueueResult(listQueueResult: Elem): Seq[Queue] = {
    (listQueueResult \\ "QueueUrl").map(queueUrl => new Queue(queueUrl.text))
  }
}
