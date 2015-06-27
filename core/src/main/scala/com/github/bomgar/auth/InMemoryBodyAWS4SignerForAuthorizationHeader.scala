package com.github.bomgar.auth

import java.time.Clock

import com.github.bomgar.Region
import com.github.bomgar.auth.credentials.AwsCredentialsProvider
import com.ning.http.client.{Request, RequestBuilderBase}

class InMemoryBodyAWS4SignerForAuthorizationHeader(awsCredentialsProvider: AwsCredentialsProvider,
                                                   region: Region.Type,
                                                   serviceName: String,
                                                   clock: Clock = java.time.Clock.systemUTC(),
                                                   body: Array[Byte]
                                                    )
  extends AWS4SignerForAuthorizationHeader(awsCredentialsProvider, region, serviceName, clock) {
  override def calculateAndAddSignature(request: Request, requestBuilderBase: RequestBuilderBase[_]): Unit =
    super.calculateAndAddSignature(request, requestBuilderBase, body)
}
