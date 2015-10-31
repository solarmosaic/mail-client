package com.solarmosaic.client.mail.content

import java.io.File
import java.nio.charset.Charset
import javax.mail.internet.{MimeMultipart, MimeBodyPart}

import com.solarmosaic.client.mail.content.ContentType.MultipartTypes.MultipartType
import com.solarmosaic.client.mail.content.ContentType.TextTypes.TextType
import com.solarmosaic.client.mail.content.ContentType.{MultipartTypes, TextTypes}
import com.solarmosaic.client.mail.message.Header

sealed trait Content
sealed trait BodyPartContent extends Content {
  def bodyPart: MimeBodyPart
}

case class Attachment(
  file: File,
  filename: Option[String] = None
) extends BodyPartContent {
  def bodyPart: MimeBodyPart = {
    val mbp = new MimeBodyPart
    mbp.attachFile(file)
    filename.foreach(mbp.setFileName)
    mbp
  }
}

case class MultipartContent(
  content: Seq[BodyPartContent],
  subType: MultipartType = MultipartTypes.mixed
) extends Content with ContentType.Multipart {
  def multipart: MimeMultipart = {
    val mm = new MimeMultipart(subType.toString)
    content.foreach(c => mm.addBodyPart(c.bodyPart))
    mm
  }

  def withAttachment(attachment: Attachment): MultipartContent = withContent(attachment)

  def withHtml(html: Text): MultipartContent = withContent(html)

  def withContent(content: BodyPartContent): MultipartContent = copy(content = this.content :+ content)

  def withText(text: Text): MultipartContent = withContent(text)
}

case class Text(
  text: String,
  subType: TextType = TextTypes.plain,
  charset: Charset = Charset.defaultCharset,
  headers: List[Header] = List.empty[Header]
) extends BodyPartContent with ContentType.Text {
  def bodyPart: MimeBodyPart = {
    val mbp = new MimeBodyPart
    mbp.setText(text, charset.name, subType.toString)
    headers.foreach(header => mbp.addHeader(header.name, header.value))
    mbp
  }
}

object Html {
  def apply(
    html: String,
    charset: Charset = Charset.defaultCharset,
    headers: List[Header] = List.empty[Header]
  ): Text = Text(html, TextTypes.html, charset, headers)
}
