package com.github.bomgar.auth.credentials

case class BasicAwsCredentials(awsAccessKeyId: String, awsSecretKey: String) extends AwsCredentials
