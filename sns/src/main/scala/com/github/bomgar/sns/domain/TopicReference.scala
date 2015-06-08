package com.github.bomgar.sns.domain

import scala.xml.Elem

case class TopicReference(
                           topicName: String,
                           topicArn: String)

object TopicReference {

  def fromCreateTopicResult(createTopicResult: Elem): TopicReference = {
    val topicArn = (createTopicResult \\ "TopicArn").map(_.text).head
    val topicName = extractTopicNameFromTopicArn(topicArn).
      getOrElse(throw new IllegalArgumentException("Could not parse TopicName"))
    new TopicReference(topicName,topicArn)
  }

  def fromListTopicResult(listTopicResult: Elem): Seq[TopicReference] = {
    for (topicArn <- (listTopicResult \\ "TopicArn").map(_.text)) yield {
      val topicName = extractTopicNameFromTopicArn(topicArn).
        getOrElse(throw new IllegalArgumentException("Could not parse TopicName"))
      new TopicReference(topicName,topicArn)
    }
  }

  private def extractTopicNameFromTopicArn (topicArn:String): Option[String] = {
    // Debate: Better to throw IllegalArgumentException for missing name here? ..and return plain String?
    // AWS constraints: "Topic names must be made up of only uppercase and lowercase ASCII letters, numbers, underscores, and hyphens, and must be between 1 and 256 characters long."
    val regExForName = """^.+:([\p{Alnum}_-]{1,256}+)$""".r
    topicArn match {
      case regExForName(group) => Option(group)
      case _ => None
    }
  }
}
