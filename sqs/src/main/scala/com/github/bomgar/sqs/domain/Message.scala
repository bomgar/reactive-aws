package com.github.bomgar.sqs.domain

import scala.xml.Elem

case class Message(
                    body: String,
                    messageId: String,
                    md5OfMessageBody: String,
                    receiptHandle: String
                    )


object Message {
  def fromReceiveMessageResult(response: Elem): Seq[Message] = {
    (response \\ "Message").map {
      messageElem =>
        Message(
          body = (messageElem \ "Body").text,
          messageId = (messageElem \ "MessageId").text,
          md5OfMessageBody = (messageElem \ "MD5OfBody").text,
          receiptHandle = (messageElem \ "ReceiptHandle").text
        )
    }
  }
}
