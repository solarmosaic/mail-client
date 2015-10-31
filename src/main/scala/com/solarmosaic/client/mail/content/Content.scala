package com.solarmosaic.client.mail.content

import java.io.File
import java.nio.charset.Charset
import javax.mail.internet.{MimeBodyPart, MimeMultipart}

import com.solarmosaic.client.mail.content.ContentType.MultipartTypes.MultipartType
import com.solarmosaic.client.mail.content.ContentType.TextTypes.TextType
import com.solarmosaic.client.mail.content.ContentType.{MultipartTypes, TextTypes}
import com.solarmosaic.client.mail.{Envelope, Header}

/** Common base trait for [[Envelope]] content. */
sealed trait Content

/** Common base trait for [[MimeBodyPart]] content. */
sealed trait ContentPart extends Content {

  /** Returns the [[MimeBodyPart]] representation of the content. */
  def part: MimeBodyPart
}

/**
 * File attachment content.
 *
 * @param file The file to attach.
 * @param filename The name to give the attachment. Defaults to the given files name.
 * @param headers Headers to add to the content.
 */
case class Attachment(
  file: File,
  filename: Option[String] = None,
  headers: List[Header] = List.empty[Header]
) extends ContentPart {
  def part: MimeBodyPart = {
    val mbp = new MimeBodyPart
    mbp.attachFile(file)
    filename.foreach(mbp.setFileName)
    headers.foreach(header => mbp.addHeader(header.name, header.value))
    mbp
  }
}

/**
 * Multipart content.
 *
 * @param parts A sequence of [[ContentPart]]'s.
 * @param subType The sub-type of the multipart, ie "mixed".
 */
case class Multipart(
  parts: Seq[ContentPart],
  subType: MultipartType = MultipartTypes.mixed
) extends Content with ContentType.Multipart {

  /** Returns the [[Multipart]] with the given [[ContentPart]](s) appended to its content. */
  def append(part: ContentPart*): Multipart = copy(parts = this.parts ++ part)

  /** Returns the [[MimeMultipart]] representation of the content. */
  def multipart: MimeMultipart = {
    val mm = new MimeMultipart(subType.toString)
    parts.foreach(part => mm.addBodyPart(part.part))
    mm
  }
}

object Multipart {

  /** Convenience constructor for a mixed [[Multipart]] with a varying number of [[ContentPart]]s. */
  def apply(content: ContentPart*): Multipart = Multipart(content)
}

/**
 * Text content.
 *
 * @param text The text.
 * @param subType The sub-type of the text, ie "plain"
 * @param charset The character set to use for the given text.
 * @param headers Headers to add to the content.
 */
case class Text(
  text: String,
  subType: TextType = TextTypes.plain,
  charset: Charset = Charset.defaultCharset,
  headers: List[Header] = List.empty[Header]
) extends ContentPart with ContentType.Text {
  def part: MimeBodyPart = {
    val mbp = new MimeBodyPart
    mbp.setText(text, charset.name, subType.toString)
    headers.foreach(header => mbp.addHeader(header.name, header.value))
    mbp
  }
}

/** Html content. */
object Html {

  /** Convenience method for creating [[Text]] with a sub-type of "html". */
  def apply(
    html: String,
    charset: Charset = Charset.defaultCharset,
    headers: List[Header] = List.empty[Header]
  ): Text = Text(html, TextTypes.html, charset, headers)
}
