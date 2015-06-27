package com.github.bomgar.client

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.{BasicAwsCredentialsProvider, BasicAwsCredentials}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.libs.ws._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.Future
import scala.xml.Elem
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.Status._
import scala.concurrent.duration._

class BaseAwsClientTest extends Specification with Mockito with FutureAwaits with DefaultAwaitTimeout {

  val awsCredentials = new BasicAwsCredentials("key", "secret")

  val credentialsProvider = new BasicAwsCredentialsProvider(awsCredentials)

  class TestAwsClient(wsClient: WSClient) extends BaseAwsClient(credentialsProvider, Region.AP_NORTHEAST_1, wsClient, "test", 2.seconds) {
    override def executeFormEncodedAction(actionParameters: Map[String, String], url: String, timeout: Duration): Future[Elem] =
      super.executeFormEncodedAction(actionParameters, url, timeout)
  }

  "A base aws client" should {
    "execute form encoded requests" in {
      val (wsClient: WSClient, wsResponse: WSResponse) = givenAMockedWsClient

      val xmlResult = <a>test</a>
      wsResponse.status returns OK
      wsResponse.xml returns xmlResult


      val awsClient = new TestAwsClient(wsClient)

      val actionParameters = Map(
        "Action" -> "ListQueues",
        "Version" -> "2012-11-05"
      )

      val responseXml = await(awsClient.executeFormEncodedAction(actionParameters))
      responseXml should be equalTo xmlResult
    }

    "handle error codes" in {
      val (wsClient: WSClient, wsResponse: WSResponse) = givenAMockedWsClient

      wsResponse.status returns NOT_FOUND


      val awsClient = new TestAwsClient(wsClient)

      val actionParameters = Map(
        "Action" -> "ListQueues",
        "Version" -> "2012-11-05"
      )

      await(awsClient.executeFormEncodedAction(actionParameters)) must throwA[AwsCallFailedException]
    }
  }

  private def givenAMockedWsClient: (WSClient, WSResponse) = {
    val wsClient = mock[WSClient]
    val wsRequest = mock[WSRequest]
    val wsResponse = mock[WSResponse]
    val wsResponseFuture = Future.successful(wsResponse)

    wsClient.url(anyString) returns wsRequest
    wsRequest.sign(any[WSSignatureCalculator]) returns wsRequest
    wsRequest.withHeaders(anyVarArg[(String, String)]) returns wsRequest
    wsRequest.withRequestTimeout(anyInt) returns wsRequest
    wsRequest.post(anyString)(any) returns wsResponseFuture
    (wsClient, wsResponse)
  }
}
