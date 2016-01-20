package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification

class TopicReferenceTest extends Specification {
  "A TopicReference" should {

    "parse create topic result" in {
      val createTopicResult =
        <CreateTopicResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <CreateTopicResult>
            <TopicArn>arn:aws:sns:us-east-1:123456789012:My-Topic</TopicArn>
          </CreateTopicResult>
          <ResponseMetadata>
            <RequestId>a8dec8b3-33a4-11df-8963-01868b7c937a</RequestId>
          </ResponseMetadata>
        </CreateTopicResponse>

      val topic = TopicReference.fromCreateTopicResult(createTopicResult)

      topic.topicArn must be equalTo "arn:aws:sns:us-east-1:123456789012:My-Topic"
    }

    "parse parse a list of topics" in {
      val listTopicsResult =
        <ListTopicsResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
          <ListTopicsResult>
            <Topics>
              <member>
                <TopicArn>arn:aws:sns:us-east-1:123456789011:My-Topic1</TopicArn>
              </member>
              <member>
                <TopicArn>arn:aws:sns:us-east-1:123456789012:My-Topic2</TopicArn>
              </member>
              <member>
                <TopicArn>arn:aws:sns:us-east-1:123456789013:My-Topic3</TopicArn>
              </member>
            </Topics>
          </ListTopicsResult>
          <ResponseMetadata>
            <RequestId>3f1478c7-33a9-11df-9540-99d0768312d3</RequestId>
          </ResponseMetadata>
        </ListTopicsResponse>

      val topics = TopicReference.fromListTopicsResult(listTopicsResult)
      topics must contain(TopicReference("arn:aws:sns:us-east-1:123456789011:My-Topic1"))
      topics must contain(TopicReference("arn:aws:sns:us-east-1:123456789012:My-Topic2"))
      topics must contain(TopicReference("arn:aws:sns:us-east-1:123456789013:My-Topic3"))
    }

  }
}
