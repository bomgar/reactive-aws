package com.github.bomgar.client

import play.api.libs.ws.WSResponse


class AwsCallFailedException(response: WSResponse) extends RuntimeException
