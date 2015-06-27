package com.github.bomgar.auth

import java.nio.charset.StandardCharsets
import java.time.{Clock, Instant, ZoneId}

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentials, BasicAwsCredentialsProvider}
import com.github.bomgar.utils.BinaryUtils
import com.ning.http.client.{RequestBuilderBase, FluentCaseInsensitiveStringsMap, Request, Param}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.collection.JavaConverters._
import scala.language.existentials

class AWS4SignerForAuthorizationHeaderTest extends Specification with Mockito {

  "A authorization header signerer" should {
    "sign requests" in {

      val clock = Clock.fixed(Instant.ofEpochMilli(121212), ZoneId.of("UTC"))

      val request = mock[Request]
      val requestBuilderBase = mock[RequestBuilderBase[_]]

      val body: Array[Byte] = "Hello World!".getBytes(StandardCharsets.UTF_8)

      request.getHeaders returns new FluentCaseInsensitiveStringsMap().add("testHeader", "test")
      request.getQueryParams returns List(new Param("testQuery", "test")).asJava

      request.getUrl returns "http://test/test"

      val awsCredentials = new BasicAwsCredentials("key", "secret")
      val signer = new InMemoryBodyAWS4SignerForAuthorizationHeader(new BasicAwsCredentialsProvider(awsCredentials), Region.EU_WEST_1, "testService", clock, body)

      signer.calculateAndAddSignature(request, requestBuilderBase)

      there was one(requestBuilderBase).setHeader("x-amz-date", "19700101T000201Z")
      there was one(requestBuilderBase).setHeader("Host", "test")
      there was one(requestBuilderBase).setHeader("Authorization",
        "AWS4-HMAC-SHA256 Credential=key/19700101/eu-west-1/testService/aws4_request, " +
          "SignedHeaders=host;testheader;x-amz-date, " +
          "Signature=349c6cdd9e884c4be05c0049b3529d6d5d5bb68c02c9cd7dee03cc03ec7c5b0d"
      )

    }
  }
}
