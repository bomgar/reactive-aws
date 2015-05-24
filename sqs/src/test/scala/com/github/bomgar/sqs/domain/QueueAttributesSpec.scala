package com.github.bomgar.sqs.domain

import org.specs2.mutable.Specification

class QueueAttributesSpec extends Specification {

  "Queue attributes" should {
    "parse get queue attributes result result" in {
      val getQueueAttributesResponse =
        <GetQueueAttributesResponse xmlns="http://queue.amazonaws.com/doc/2012-11-05/">
          <GetQueueAttributesResult>
            <Attribute>
              <Name>ApproximateNumberOfMessages</Name>
              <Value>0</Value>
              <Name>Other</Name>
              <Value>0</Value>
            </Attribute>
          </GetQueueAttributesResult>
          <ResponseMetadata>
            <RequestId>2ef6ba32-c929-54db-9eaa-f7e91603550d</RequestId>
          </ResponseMetadata>
        </GetQueueAttributesResponse>

      val queueAttributes = QueueAttributes.fromGetQueueAttributesResponse(getQueueAttributesResponse)

      queueAttributes.attributes must havePair("ApproximateNumberOfMessages" -> "0")
      queueAttributes.attributes must havePair("Other" -> "0")
    }
  }
}
