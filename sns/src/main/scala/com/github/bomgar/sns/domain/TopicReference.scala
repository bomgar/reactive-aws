package com.github.bomgar.sns.domain

import scala.xml.Elem

case class TopicReference(topicArn: String)

object TopicReference {

  def fromCreateTopicResult(createTopicResult: Elem): TopicReference = {
    val topicArn = (createTopicResult \\ "TopicArn").map(_.text).head
    new TopicReference(topicArn)
  }

  def fromListTopicsResult(listTopicResult: Elem): Seq[TopicReference] = {
     (listTopicResult \\ "TopicArn").map(_.text).map(TopicReference.apply)
  }

}
