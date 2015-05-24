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
            </Attribute>
            <Attribute>
              <Name>Other</Name>
              <Value>0</Value>
            </Attribute>
          </GetQueueAttributesResult>
          <ResponseMetadata>
            <RequestId>2ef6ba32-c929-54db-9eaa-f7e91603550d</RequestId>
          </ResponseMetadata>
        </GetQueueAttributesResponse>

      val queueAttributes = QueueAttributes.fromGetQueueAttributesResponse(getQueueAttributesResponse)

      queueAttributes.approximateNumberOfMessages must beSome("0")
      queueAttributes.approximateNumberOfMessagesDelayed must beNone
      queueAttributes.visibilityTimeout must beNone
      queueAttributes.lastModifiedTimestamp must beNone
      queueAttributes.policy must beNone
      queueAttributes.maximumMessageSize must beNone
      queueAttributes.messageRetentionPeriod must beNone
      queueAttributes.queueArn must beNone
      queueAttributes.delaySeconds must beNone
      queueAttributes.receiveMessageWaitTimeSeconds must beNone
      queueAttributes.redrivePolicy must beNone
      queueAttributes.attributes must havePair("Other" -> "0")
    }
  }
}
