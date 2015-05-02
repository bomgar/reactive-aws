package com.github.bomgar

object Region extends Enumeration {
  type Type = Value

  val GovCloud = Value("us-gov-west-1")
  val US_EAST_1 = Value("us-east-1")
  val US_WEST_1 = Value("us-west-1")
  val US_WEST_2 = Value("us-west-2")
  val EU_WEST_1 = Value("eu-west-1")
  val EU_CENTRAL_1 = Value("eu-central-1")
  val AP_SOUTHEAST_1 = Value("ap-southeast-1")
  val AP_SOUTHEAST_2 = Value("ap-southeast-2")
  val AP_NORTHEAST_1 = Value("ap-northeast-1")
  val SA_EAST_1 = Value("sa-east-1")
  val CN_NORTH_1 = Value("cn-north-1")

}
