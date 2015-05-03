package com.github.bomgar.client

import java.io.StringReader

import scala.xml.{Elem, XML}


case class AwsCallFailedException(
                                   status: Int,
                                   message: Option[String],
                                   errorType: Option[String],
                                   errorCode: Option[String],
                                   detail: Option[String]
                                   ) extends RuntimeException

object AwsCallFailedException {
  def fromErrorResponse(status: Int, responseBody: String): AwsCallFailedException = {
    try {
      val responseAsXml = XML.load(new StringReader(responseBody))
      new AwsCallFailedException(
        status = status,
        message = getElemText(responseAsXml, "Message"),
        errorType = getElemText(responseAsXml, "Type"),
        detail = getElemText(responseAsXml, "Detail"),
        errorCode = getElemText(responseAsXml, "Code")
      )
    } catch {
      case e: Exception =>
        val ex = new AwsCallFailedException(status, Some("Unknown Error: Response body not valid xml"), None, None, None)
        ex.initCause(e)
        ex
    }
  }

  private def getElemText(responseAsXml: Elem, elemetName: String): Option[String] = {
    (responseAsXml \\ elemetName).headOption.map(_.text).filter(!_.isEmpty)
  }
}
