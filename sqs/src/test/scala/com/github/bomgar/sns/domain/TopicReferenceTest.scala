package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification

class TopicReferenceTest extends Specification {
  "A TopicReference" should {

    "parse create queue result" in {
      val createQueueResult =
        <CreateTopicResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <CreateTopicResult>
            <TopicArn>arn:aws:sns:us-east-1:123456789012:My-Topic</TopicArn>
          </CreateTopicResult>
          <ResponseMetadata>
            <RequestId>a8dec8b3-33a4-11df-8963-01868b7c937a</RequestId>
          </ResponseMetadata>
        </CreateTopicResponse>

      val topicArn = TopicReference.fromCreateTopicResult(createQueueResult).topicArn

      topicArn must be equalTo "arn:aws:sns:us-east-1:123456789012:My-Topic"
    }

  }
}
