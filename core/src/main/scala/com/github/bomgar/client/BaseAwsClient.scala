package com.github.bomgar.client

import com.github.bomgar.Region
import com.github.bomgar.auth.AWS4SignerForAuthorizationHeader
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import play.api.libs.ws.WSClient

class BaseAwsClient(
                     val credentialsProvider: AwsCredentialsProvider,
                     val region: Region.Type,
                     val client: WSClient,
                     val serviceName: String
                     ) {

  val baseUrl = s"https://$serviceName.$region.amazonaws.com"

  val signer = new AWS4SignerForAuthorizationHeader(credentialsProvider, region, serviceName)
}
