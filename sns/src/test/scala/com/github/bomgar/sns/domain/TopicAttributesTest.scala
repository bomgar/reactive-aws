package com.github.bomgar.sns.domain

import org.specs2.mutable.Specification
import play.api.libs.json.JsValue

class TopicAttributesTest extends Specification {
  "A TopicAttribute" should {
    "parse get topic attributes result" in {
      val getTopicAttributesResponse =
        <GetTopicAttributesResponse xmlns="http://sns.amazonaws.com/doc/2010-03-31/">
         <GetTopicAttributesResult>
           <Attributes>
             <entry>
               <key>Policy</key>
               <value>{{&quot;Version&quot;:&quot;2008-10-17&quot;,&quot;Id&quot;:&quot;__default_policy_ID&quot;,&quot;Statement&quot;:[{{&quot;Sid&quot;:&quot;__default_statement_ID&quot;,&quot;Effect&quot;:&quot;Allow&quot;,&quot;Principal&quot;:{{&quot;AWS&quot;:&quot;*&quot;}},&quot;Action&quot;:[&quot;SNS:Subscribe&quot;,&quot;SNS:ListSubscriptionsByTopic&quot;,&quot;SNS:DeleteTopic&quot;,&quot;SNS:GetTopicAttributes&quot;,&quot;SNS:Publish&quot;,&quot;SNS:RemovePermission&quot;,&quot;SNS:AddPermission&quot;,&quot;SNS:Receive&quot;,&quot;SNS:SetTopicAttributes&quot;],&quot;Resource&quot;:&quot;arn:aws:sns:eu-central-1:370621384784:test&quot;,&quot;Condition&quot;:{{&quot;StringEquals&quot;:{{&quot;AWS:SourceOwner&quot;:&quot;370621384784&quot;}}}}}}]}}</value>
             </entry>
             <entry>
               <key>Owner</key>
              <value>370621384784</value>
             </entry>
             <entry>
               <key>SubscriptionsPending</key>
               <value>0</value>
          </entry>
             <entry>
               <key>TopicArn</key>
               <value>arn:aws:sns:eu-central-1:370621384784:test</value>
             </entry>             <entry>
               <key>EffectiveDeliveryPolicy</key>
               <value>{{&quot;http&quot;:{{&quot;defaultHealthyRetryPolicy&quot;:{{&quot;minDelayTarget&quot;:20,&quot;maxDelayTarget&quot;:20,&quot;numRetries&quot;:3,&quot;numMaxDelayRetries&quot;:0,&quot;numNoDelayRetries&quot;:0,&quot;numMinDelayRetries&quot;:0,&quot;backoffFunction&quot;:&quot;linear&quot;}},&quot;disableSubscriptionOverrides&quot;:false}}}}</value>
             </entry>
             <entry>
               <key>SubscriptionsConfirmed</key>
               <value>0</value>
             </entry>
             <entry>
               <key>DisplayName</key>
               <value/>
             </entry>
             <entry>
               <key>SubscriptionsDeleted</key>
               <value>0</value>
             </entry>
           </Attributes>
         </GetTopicAttributesResult>
         <ResponseMetadata>
           <RequestId>aa5f13b3-8337-5d5c-bd5d-5e45ca4ba926</RequestId>
         </ResponseMetadata>
       </GetTopicAttributesResponse>
      val topicAttributes = TopicAttributes.fromGetQueueAttributesResponse(getTopicAttributesResponse)
      val policy = topicAttributes.policy
      val effectiveDeliveryPolicy = topicAttributes.effectiveDeliveryPolicy
      val deliveryPolicy = topicAttributes.deliveryPolicy

      policy.map(_ \ "Version" toString) must beSome ("\"2008-10-17\"")
      topicAttributes.owner must beSome(370621384784L)
      topicAttributes.topicArn must beSome ("arn:aws:sns:eu-central-1:370621384784:test")
      effectiveDeliveryPolicy.map(_ \ "http") must beSome
      topicAttributes.subscriptionsConfirmed must beSome(0L)
      topicAttributes.displayName must beSome("")
      topicAttributes.subscriptionsDeleted must beSome(0L)
      deliveryPolicy must beNone
    }
  }

}
