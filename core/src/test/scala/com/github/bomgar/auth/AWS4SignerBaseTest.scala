package com.github.bomgar.auth

import java.net.URL

import org.specs2.mutable.Specification


class AWS4SignerBaseTest extends Specification {

  "A aws base signer" should {
    "created canonical requests" in {
      val headers: Map[String, String] = Map(
      "TestHEader" -> "testHeader",
      "testHeader" -> "testHeader",
      "testHEader" -> "testHeader"
      )

      val queryParams: Map[String, String] = Map("testParam" -> "testParam")

      val signer = new AWS4SignerBase()

      val canonicalizedHeaderString: String = signer.getCanonicalizedHeaderString(headers)
      val canonicalizedQueryString: String = signer.getCanonicalizedQueryString(queryParams)
      val canonicalizeHeaderNames: String = signer.getCanonicalizeHeaderNames(headers)

      canonicalizedHeaderString must be equalTo """|testheader:testHeader
                                                   |testheader:testHeader
                                                   |testheader:testHeader
                                                   |""".stripMargin
      canonicalizedQueryString must be equalTo "testParam=testParam"
      canonicalizeHeaderNames must be equalTo "testheader;testheader;testheader"

      val request: String = signer.getCanonicalRequest(new URL("http://test.de/moep/blubb/"), "POST", canonicalizedQueryString, canonicalizeHeaderNames, canonicalizedHeaderString, "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069")
      request must be equalTo
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
