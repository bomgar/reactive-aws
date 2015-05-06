package com.github.bomgar.sqs.domain

import scala.xml.Elem

case class MessageReference(messageId: String, md5OfMessageBody: String)

object MessageReference {
  def fromSendMessageResponse(response: Elem): MessageReference = {
    MessageReference(
      messageId = (response \\ "MessageId").map(_.text).head,
      md5OfMessageBody = (response \\ "MD5OfMessageBody").map(_.text).head
    )
  }
}
