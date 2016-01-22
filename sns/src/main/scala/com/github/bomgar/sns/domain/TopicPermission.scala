package com.github.bomgar.sns.domain

case class TopicPermission(
                            topic: TopicReference,
                            label: String,
                            actions: List[String],
                            principalAwsIds: List[String]) {
}
