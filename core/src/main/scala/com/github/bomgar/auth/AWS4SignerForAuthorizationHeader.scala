package com.github.bomgar.auth


import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import play.api.libs.ws.{WSRequest, WSSignatureCalculator}


class AWS4SignerForAuthorizationHeader(val awsCredentialsProvider: AwsCredentialsProvider) extends AWS4SignerBase with WSSignatureCalculator {


  override def sign(request: WSRequest): Unit = {

  }

}
