package com.github.bomgar.sns.domain

abstract class PagedResult {
  def nextPageToken: Option[String]
}
