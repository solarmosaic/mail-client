package com.solarmosaic.client.mail.content

import com.solarmosaic.client.mail.content.ContentType.MultipartTypes.MultipartType
import com.solarmosaic.client.mail.content.ContentType.PrimaryTypes.PrimaryType
import com.solarmosaic.client.mail.content.ContentType.TextTypes.TextType

trait ContentType[T] {
  val primaryType: PrimaryType
  val subType: T
  override def toString: String = s"$primaryType/$subType"
}

object ContentType {
  object PrimaryTypes extends Enumeration {
    type PrimaryType = Value

    val multipart = Value("multipart")
    val text = Value("text")
  }

  object MultipartTypes extends Enumeration {
    type MultipartType = Value

    val alternative = Value("alternative")
    val mixed = Value("mixed")
    val related = Value("related")
  }

  object TextTypes extends Enumeration {
    type TextType = Value

    val html = Value("html")
    val plain = Value("plain")
  }

  trait Multipart extends ContentType[MultipartType] {
    val primaryType = PrimaryTypes.multipart
  }

  trait Text extends ContentType[TextType] {
    val primaryType = PrimaryTypes.text
  }
}
