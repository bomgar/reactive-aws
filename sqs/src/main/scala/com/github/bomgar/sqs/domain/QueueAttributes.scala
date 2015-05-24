package com.github.bomgar.sqs.domain

import scala.xml.Elem

case class QueueAttributes(attributes: Map[String, String])

object QueueAttributes {
  def fromGetQueueAttributesResponse(response: Elem): QueueAttributes = {
    val attributes = (response \\ "Attribute").map{attribute =>
      val name = (attribute \ "Name").text
      val value = (attribute \ "Value").text
      name -> value
    }.toMap
    new QueueAttributes(attributes)
  }
}