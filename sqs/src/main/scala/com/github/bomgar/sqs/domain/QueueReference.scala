package com.github.bomgar.sqs.domain

import scala.xml.Elem

case class QueueReference(url: String)

object QueueReference {
  def fromListQueueResult(listQueueResult: Elem): Seq[QueueReference] = {
    (listQueueResult \\ "QueueUrl").map(queueUrl => new QueueReference(queueUrl.text))
  }
}
