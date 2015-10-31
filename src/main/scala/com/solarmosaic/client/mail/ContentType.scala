package com.solarmosaic.client.mail

trait ContentType {
  val primaryType: String
  val subType: String
  override def toString: String = s"$primaryType/$subType"
}

case class BodyPartContentType(primaryType: String, subType: String) extends ContentType

case class MultipartContentType(subType: String) extends ContentType {
  val primaryType = "multipart"
}

object MimeContentType {
  val `multipart/alternative` = MultipartContentType("alternative")
  val `multipart/mixed` = MultipartContentType("mixed")
  val `multipart/related` = MultipartContentType("related")
  val `text/html` = BodyPartContentType("text", "html")
  val `text/plain` = BodyPartContentType("text", "plain")
}
