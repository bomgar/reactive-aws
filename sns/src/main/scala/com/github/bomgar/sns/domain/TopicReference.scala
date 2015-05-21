package com.github.bomgar.sns.domain

import scala.xml.Elem

case class TopicReference(topicArn: String)

object TopicReference {

  def fromCreateTopicResult(createTopicResult: Elem): TopicReference = {
    (createTopicResult \\ "TopicArn").map(topicArn => new TopicReference(topicArn.text)).head
  }
}
