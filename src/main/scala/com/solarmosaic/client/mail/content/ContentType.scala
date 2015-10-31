package com.solarmosaic.client.mail.content

import com.solarmosaic.client.mail.content.ContentType.MultipartTypes.MultipartType
import com.solarmosaic.client.mail.content.ContentType.PrimaryTypes.PrimaryType
import com.solarmosaic.client.mail.content.ContentType.TextTypes.TextType

/**
 * Provides content type information to [[Content]] classes.
 * @tparam T The type of sub-type.
 */
trait ContentType[T] {
  val primaryType: PrimaryType
  val subType: T

  def contentType: String = s"$primaryType/$subType"

  override def toString: String = contentType
}

object ContentType {

  /** Valid primary content types. */
  object PrimaryTypes extends Enumeration {
    type PrimaryType = Value

    val multipart = Value("multipart")
    val text = Value("text")
  }

  /** Valid multipart content sub-types. */
  object MultipartTypes extends Enumeration {
    type MultipartType = Value

    val alternative = Value("alternative")
    val mixed = Value("mixed")
    val related = Value("related")
  }

  /** Valid text content sub-types. */
  object TextTypes extends Enumeration {
    type TextType = Value

    val html = Value("html")
    val plain = Value("plain")
  }

  /** Base trait for multipart [[Content]]. */
  trait Multipart extends ContentType[MultipartType] {
    val primaryType = PrimaryTypes.multipart
  }

  /** Base trait for text [[Content]] */
  trait Text extends ContentType[TextType] {
    val primaryType = PrimaryTypes.text
  }
}
