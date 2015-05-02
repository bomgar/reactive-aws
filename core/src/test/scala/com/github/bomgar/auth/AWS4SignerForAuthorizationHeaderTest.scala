package com.github.bomgar.auth

import java.nio.charset.StandardCharsets
import java.time.{Clock, Instant, ZoneId}

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.libs.ws.WSRequest

class AWS4SignerForAuthorizationHeaderTest extends Specification with Mockito {

  "A authorization header signerer" should {
    "sign requests" in {

      val clock = Clock.fixed(Instant.ofEpochMilli(121212), ZoneId.of("UTC"))

      val request = mock[WSRequest]
      request.setHeader(anyString, anyString) returns request

      request.allHeaders returns Map("testHeader" -> Seq("test"))
      request.queryString returns Map("testQuery" -> Seq("test"))
      request.url returns "http://test/test"
      request.getBody returns Some("Hello World!".getBytes(StandardCharsets.UTF_8))

      val awsCredentials = new BasicAwsCredentials("key", "secret")
      val signer = new AWS4SignerForAuthorizationHeader(new BasicAwsCredentialsProvider(awsCredentials), Region.EU_WEST_1, "testService", clock)

      signer.sign(request)

      there was one(request).setHeader("x-amz-date", "19700101T000201Z")
      there was one(request).setHeader("Host", "test")
      there was one(request).setHeader("Authorization",
        "AWS4-HMAC-SHA256 Credential=key/19700101/eu-west-1/testService/aws4_request, " +
          "SignedHeaders=host;testheader;x-amz-date, " +
          "Signature=349c6cdd9e884c4be05c0049b3529d6d5d5bb68c02c9cd7dee03cc03ec7c5b0d"
      )

    }
  }
}
