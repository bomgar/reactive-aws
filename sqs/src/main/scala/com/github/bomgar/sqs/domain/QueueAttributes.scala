package com.github.bomgar.sqs.domain

import scala.xml.Elem

case class QueueAttributes(attributes: Map[String, String])

object QueueAttributes {
  def fromGetQueueAttributesResponse(response: Elem): QueueAttributes = {
    ???
  }
}