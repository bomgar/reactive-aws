package com.github.bomgar.auth.credentials

trait AwsCredentials {

  /**
   * Returns the AWS access key ID for this credentials object.
   *
   * @return The AWS access key ID for this credentials object.
   */
  def awsAccessKeyId: String

  /**
   * Returns the AWS secret access key for this credentials object.
   *
   * @return The AWS secret access key for this credentials object.
   */
  def awsSecretKey: String
}
