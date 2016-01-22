package com.github.bomgar.sns.domain

import scala.xml.Elem

case class TopicReferenceListResult(
                                   override val nextPageToken: Option[String],
                                   topicReferences: Seq[TopicReference]
                                 ) extends PagedResult {}

object TopicReferenceListResult {

  def fromListTopicsResult (listTopicsResult: Elem) : TopicReferenceListResult = new TopicReferenceListResult(
    (listTopicsResult \\ "NextToken").map(_.text).headOption,
    TopicReference.fromListTopicsResult(listTopicsResult)
  )
}
