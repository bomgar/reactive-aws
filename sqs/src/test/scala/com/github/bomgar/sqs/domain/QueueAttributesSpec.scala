package com.github.bomgar.sqs.domain

import java.time.{Instant, Duration}

import org.specs2.mutable.Specification

class QueueAttributesSpec extends Specification {

  "Queue attributes" should {
    "parse get queue attributes result result" in {
      val getQueueAttributesResponse =
        <GetQueueAttributesResponse xmlns="http://queue.amazonaws.com/doc/2012-11-05/">
          <GetQueueAttributesResult>
            <Attribute>
              <Name>QueueArn</Name> <Value>arn:aws:sqs:eu-west-1:474941031906:test-queue</Value>
            </Attribute>
            <Attribute>
              <Name>ApproximateNumberOfMessages</Name>
              <Value>0</Value>
            </Attribute>
            <Attribute>
              <Name>ApproximateNumberOfMessagesNotVisible</Name> <Value>0</Value>
            </Attribute>
            <Attribute>
              <Name>ApproximateNumberOfMessagesDelayed</Name>
              <Value>0</Value>
            </Attribute>
            <Attribute>
              <Name>CreatedTimestamp</Name>
              <Value>1432476645</Value>
            </Attribute>
            <Attribute>
              <Name>LastModifiedTimestamp</Name>
              <Value>1432476645</Value>
            </Attribute>
            <Attribute>
              <Name>VisibilityTimeout</Name>
              <Value>30</Value>
            </Attribute>
            <Attribute>
              <Name>MaximumMessageSize</Name>
              <Value>262144</Value>
            </Attribute>
            <Attribute>
              <Name>MessageRetentionPeriod</Name>
              <Value>345600</Value>
            </Attribute>
            <Attribute>
              <Name>DelaySeconds</Name>
              <Value>0</Value>
            </Attribute>
            <Attribute>
              <Name>ReceiveMessageWaitTimeSeconds</Name>
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

      queueAttributes.approximateNumberOfMessages must beSome(0)
      queueAttributes.approximateNumberOfMessagesDelayed must beSome(0)
      queueAttributes.visibilityTimeout must beSome(Duration.ofSeconds(30))
      queueAttributes.lastModifiedTimestamp must beSome(Instant.ofEpochSecond(1432476645))
      queueAttributes.policy must beNone
      queueAttributes.maximumMessageSize must beSome(262144)
      queueAttributes.messageRetentionPeriod must beSome(Duration.ofHours(96))
      queueAttributes.queueArn must beSome("arn:aws:sqs:eu-west-1:474941031906:test-queue")
      queueAttributes.delay must beSome(Duration.ofSeconds(0))
      queueAttributes.receiveMessageWaitTime must beSome(Duration.ofSeconds(0))
      queueAttributes.redrivePolicy must beNone
      queueAttributes.attributes must havePair("Other" -> "0")
    }
  }
}
