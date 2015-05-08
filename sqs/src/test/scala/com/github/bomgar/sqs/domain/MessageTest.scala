package com.github.bomgar.sqs.domain

import org.specs2.mutable.Specification


class MessageTest extends Specification {

  "A message" should {
    "parse a receive message response" in {
      val receiveMessageResponse =
        <ReceiveMessageResponse xmlns="http://queue.amazonaws.com/doc/2012-11-05/">
          <ReceiveMessageResult>
            <Message>
              <Body>hello</Body>
              <MD5OfBody>5d41402abc4b2a76b9719d911017c592</MD5OfBody>
              <ReceiptHandle>AQEBL+L7YVI5VdmzO+3tffRg6JehJ1L6SbMeBakNto5AlBwpVX/lznFQgMcpPGUcVRLuE3bj7i8aVZtBo+mMgxnKUcxhIsVSzauRnxCWz/z+FeyRCF3dvxd/3RlXUA4S7kvrxgjEldZUgrKFY40chrOhAC+zqyFjHKwOspTyObbokU3yElj7PWN5A+lp4gzyf/f8+UTv44Lx3YrSQZDBafJbMgDt4gdMfh6K9avcGPtMIo31I+8Y9pZHejbc8d9g/UVJ6+ArTmWbXmBRVgUsL4PEaP0OI5QDNcIMupM5YhCzrA0jWu6Xdxij+X29kGGLIart/0yF6J36cdQSrTz4yc6m9w==</ReceiptHandle>
              <MessageId>4ede6565-6b7c-48c7-a898-c9aeb3411a6d</MessageId>
            </Message>
            <Message>
              <Body>hello</Body>
              <MD5OfBody>5d41402abc4b2a76b9719d911017c592</MD5OfBody>
              <ReceiptHandle>AQEBMYArUMQiAk4FXAjDABECkqQO+9TRRAGhE1Cy7LfagAsFB9Qnhsx51Dfl6Hwn20+a3mTzuVSE87kdMB0WjNO1EFyNOPGjHLPVLQHTWJQsnSNLNaMVLy5HzQDKgzlcU2vlP+XtAn5iEGCGuIs1wVvkTjuSv0oGJh2MN5BKGfo2AtZ15enoPXj1qwhMMh2gMfGqoj6DHNqZVGRvYthApc9hvRvX7JE7cfkKYwMYP2tH1prat6mGZs+NdXdTSMWAvO/eJ/LcxemSRdTQbLdlr2Hj3Pd+stc6uLdWSf9+DvVmRELxTE+o+dujhKxe+gH36bZ9FZmlLS2M6SgLCMD7fIcTYw==</ReceiptHandle>
              <MessageId>b2057011-6b9e-4a89-ab8d-24a511410caa</MessageId>
            </Message>
          </ReceiveMessageResult>
          <ResponseMetadata>
            <RequestId>65b7d154-53f9-5874-a31b-9c92b2783684</RequestId>
          </ResponseMetadata>
        </ReceiveMessageResponse>

      val messages = Message.fromReceiveMessageResult(receiveMessageResponse)

      messages must have size 2
      messages must contain(
        Message(
          body = "hello",
          messageId = "4ede6565-6b7c-48c7-a898-c9aeb3411a6d",
          md5OfMessageBody = "5d41402abc4b2a76b9719d911017c592",
          receiptHandle = "AQEBL+L7YVI5VdmzO+3tffRg6JehJ1L6SbMeBakNto5AlBwpVX/lznFQgMcpPGUcVRLuE3bj7i8aVZtBo+mMgxnKUcxhIsVSzauRnxCWz/z+FeyRCF3dvxd/3RlXUA4S7kvrxgjEldZUgrKFY40chrOhAC+zqyFjHKwOspTyObbokU3yElj7PWN5A+lp4gzyf/f8+UTv44Lx3YrSQZDBafJbMgDt4gdMfh6K9avcGPtMIo31I+8Y9pZHejbc8d9g/UVJ6+ArTmWbXmBRVgUsL4PEaP0OI5QDNcIMupM5YhCzrA0jWu6Xdxij+X29kGGLIart/0yF6J36cdQSrTz4yc6m9w=="
        )
      )
      messages must contain(
        Message(
          body = "hello",
          messageId = "b2057011-6b9e-4a89-ab8d-24a511410caa",
          md5OfMessageBody = "5d41402abc4b2a76b9719d911017c592",
          receiptHandle = "AQEBMYArUMQiAk4FXAjDABECkqQO+9TRRAGhE1Cy7LfagAsFB9Qnhsx51Dfl6Hwn20+a3mTzuVSE87kdMB0WjNO1EFyNOPGjHLPVLQHTWJQsnSNLNaMVLy5HzQDKgzlcU2vlP+XtAn5iEGCGuIs1wVvkTjuSv0oGJh2MN5BKGfo2AtZ15enoPXj1qwhMMh2gMfGqoj6DHNqZVGRvYthApc9hvRvX7JE7cfkKYwMYP2tH1prat6mGZs+NdXdTSMWAvO/eJ/LcxemSRdTQbLdlr2Hj3Pd+stc6uLdWSf9+DvVmRELxTE+o+dujhKxe+gH36bZ9FZmlLS2M6SgLCMD7fIcTYw=="
        )
      )
    }
  }
}
