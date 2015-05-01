package com.github.bomgar.auth.credentials

trait AwsCredentialsProvider {

  def awsCredentials: AwsCredentials

}
