package com.github.bomgar.auth

import java.net.URL

import org.specs2.mutable.Specification


class CanonicalRequestTest extends Specification {

  "A Canonical request" should {
    "create a valid string representation" in {
      val headers: Map[String, String] = Map(
        "TestHEader" -> "testHeader",
        "testHeader" -> "testHeader",
        "testHEader" -> "testHeader"
      )

      val queryParams: Map[String, String] = Map("testParam" -> "testParam")

      val canonicalRequest = new CanonicalRequest(new URL("http://test.de/moep/blubb/"), "POST", headers, queryParams, "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069")

      canonicalRequest.canonicalizedHeaders must be equalTo """|testheader:testHeader
                                                               |testheader:testHeader
                                                               |testheader:testHeader
                                                               |""".stripMargin
      canonicalRequest.canonicalizedQueryParameters must be equalTo "testParam=testParam"
      canonicalRequest.canonicalizedHeaderNames must be equalTo "testheader;testheader;testheader"

      canonicalRequest.toString must be equalTo
        """|POST
           |/moep/blubb/
           |testParam=testParam
           |testheader:testHeader
           |testheader:testHeader
           |testheader:testHeader
           |
           |testheader;testheader;testheader
           |7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069""".stripMargin
    }
  }
}
