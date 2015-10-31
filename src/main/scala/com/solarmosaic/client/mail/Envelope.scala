package com.solarmosaic.client.mail

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Address, Message, Session}

import com.solarmosaic.client.mail.content.{Content, Multipart, Text}

/**
 * Message envelope.
 *
 * @param from The sender of the message.
 * @param to The primary recipients of the message.
 * @param subject The subject of the message.
 * @param content The [[Content]] of the message. Must be of type [[Text]] or [[Multipart]].
 * @param cc The carbon copy recipients of the message.
 * @param bcc The blind carbon copy recipients of the message.
 * @param replyTo An address to use when this message is replied to.
 * @param headers Headers to add to the message.
 */
case class Envelope(
  from: InternetAddress,
  to: Seq[InternetAddress],
  subject: String,
  content: Content,
  cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  replyTo: Seq[InternetAddress] = Seq.empty[InternetAddress],
  headers: List[Header] = List.empty[Header]
) {

  /**
   * Creates a [[MimeMessage]] for the envelope with the given [[Session]].
   * @param session The mail [[Session]] to use when creating the message.
   * @return The created message.
   */
  def message(session: Session): MimeMessage = {
    val mm = new MimeMessage(session)

    mm.setFrom(from)
    mm.setRecipients(Message.RecipientType.TO, to.toArray[Address])
    mm.setSubject(subject)

    content match {
      case c: Multipart => mm.setContent(c.multipart)
      case c: Text => mm.setText(c.text, c.charset.toString, c.subType.toString)
      case _ => throw new Exception("Envelope content is not valid.")
    }

    mm.setRecipients(Message.RecipientType.CC, cc.toArray[Address])
    mm.setRecipients(Message.RecipientType.BCC, bcc.toArray[Address])

    if (replyTo.nonEmpty) {
      mm.setReplyTo(replyTo.toArray)
    }

    headers.foreach(header => mm.addHeader(header.name, header.value))

    mm
  }
}

/** Convenience wrappers for [[Envelope]] creation. */
trait EnvelopeWrappers {
  import scala.language.implicitConversions

  /** Implicitly convert `String` to `InternetAddress`. */
  implicit def stringToInternetAddress(string: String): InternetAddress = new InternetAddress(string)

  /** Implicitly convert `String` to `Text`. */
  implicit def stringToText(string: String): Text = Text(string)
}
