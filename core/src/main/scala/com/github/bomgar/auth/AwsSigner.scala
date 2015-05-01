package com.github.bomgar.auth


import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import play.api.libs.ws.{WSRequest, WSSignatureCalculator}


class AwsSigner(val awsCredentialsProvider: AwsCredentialsProvider) extends WSSignatureCalculator {


  
  override def sign(request: WSRequest): Unit = {



  }

}
