package com.github.bomgar.sqs.domain

import org.specs2.mutable.Specification

class MessageReferenceTest extends Specification {
  "A message reference" should {
    "parse send message result" in {
      val sendMessageResponse =
        <SendMessageResponse xmlns="http://queue.amazonaws.com/doc/2012-11-05/">
          <SendMessageResult>
            <MessageId>d8bca850-2d2b-45f0-86ac-1fb5d04cd769</MessageId>
            <MD5OfMessageBody>5d41402abc4b2a76b9719d911017c592</MD5OfMessageBody>
          </SendMessageResult>
          <ResponseMetadata>
            <RequestId>3ddbe922-3f63-5f74-b3d1-a4e6509d2f82</RequestId>
          </ResponseMetadata>
        </SendMessageResponse>

      val message = MessageReference.fromSendMessageResponse(sendMessageResponse)

      message.messageId must be equalTo "d8bca850-2d2b-45f0-86ac-1fb5d04cd769"
      message.md5OfMessageBody must be equalTo "5d41402abc4b2a76b9719d911017c592"


    }
  }
}
