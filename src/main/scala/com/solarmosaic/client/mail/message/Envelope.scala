package com.solarmosaic.client.mail.message

import javax.mail.{Address, Message, Session}
import javax.mail.internet.{MimeMessage, InternetAddress}

import com.solarmosaic.client.mail.content.{Text, MultipartContent, Content}

case class Envelope(
  from: InternetAddress,
  to: Seq[InternetAddress],
  subject: String,
  content: Content,
  cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  replyTo: Option[InternetAddress] = None,
  headers: List[Header] = List.empty[Header]
) {
  def message(session: Session): MimeMessage = {
    val mm = new MimeMessage(session)

    mm.setFrom(from)
    mm.setRecipients(Message.RecipientType.TO, to.toArray[Address])
    mm.setSubject(subject)

    content match {
      case c: MultipartContent => mm.setContent(c.multipart)
      case c: Text => mm.setText(c.text, c.charset.toString, c.subType.toString)
      case _ => throw new Exception("Unsupported envelope content. Use MultipartContent or TextContent.")
    }

    mm.setRecipients(Message.RecipientType.CC, cc.toArray[Address])
    mm.setRecipients(Message.RecipientType.BCC, bcc.toArray[Address])
    replyTo.foreach(address => mm.setReplyTo(Array(address)))
    headers.foreach(header => mm.setHeader(header.name, header.value))

    mm
  }
}

trait EnvelopeWrappers {
  import scala.language.implicitConversions

  /** Implicitly convert `String` to `InternetAddress`. */
  implicit def stringToInternetAddress(string: String): InternetAddress = new InternetAddress(string)

  /** Implicitly convert `String` to `Text`. */
  implicit def stringToText(string: String): Text = Text(string)
}
