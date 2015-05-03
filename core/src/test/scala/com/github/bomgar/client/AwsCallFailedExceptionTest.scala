package com.github.bomgar.client

import org.specs2.mutable.Specification
import org.xml.sax.SAXParseException
import play.api.http.Status._

class AwsCallFailedExceptionTest extends Specification {

  "a call failed exception" should {
    "be created by a error response" in {
      val xmlBody = """|<?xml version="1.0"?>
                       |<ErrorResponse xmlns="http://queue.amazonaws.com/doc/2012-11-05/">
                       |   <Error>
                       |      <Type>Sender</Type>
                       |      <Code>InvalidParameterValue</Code>
                       |      <Message>Queue name cannot be empty</Message>
                       |      <Detail/>
                       |   </Error>
                       |   <RequestId>30ba739f-2c6f-561d-ab3b-3e5f69882995</RequestId>
                       |</ErrorResponse>
                       |""".stripMargin
      val exception = AwsCallFailedException.fromErrorResponse(BAD_REQUEST, xmlBody)
      exception.status must be equalTo BAD_REQUEST
      exception.errorType must beSome("Sender")
      exception.detail must beNone
      exception.errorCode must beSome("InvalidParameterValue")
      exception.message must beSome("Queue name cannot be empty")

      exception.getMessage must be equalTo "Queue name cannot be empty"
    }

    "be created by a error response on invalid body" in {
      val xmlBody = """<?xml vesponse>"""
      val exception = AwsCallFailedException.fromErrorResponse(BAD_REQUEST, xmlBody)
      exception.status must be equalTo BAD_REQUEST
      exception.message must beSome("Unknown Error: Response body not valid xml")
      exception.errorType must beNone
      exception.detail must beNone
      exception.errorCode must beNone

      exception.getCause must beAnInstanceOf[SAXParseException]
    }
  }

}
