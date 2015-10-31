package com.solarmosaic.client.mail

import javax.mail.{Address, Message, Session}
import javax.mail.internet.{MimeMessage, InternetAddress}

case class Envelope(
  from: InternetAddress,
  to: Seq[InternetAddress],
  subject: String,
  content: MessageParts,
  cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  replyTo: Option[InternetAddress] = None,
  headers: List[MessageHeader] = List.empty[MessageHeader]
) {
  def createMessage(session: Session): MimeMessage = {
    val message = new MimeMessage(session)

    message.setFrom(from)
    message.setRecipients(Message.RecipientType.TO, to.toArray[Address])
    message.setSubject(subject)
//    content match {
//
//    }
    message.setRecipients(Message.RecipientType.CC, cc.toArray[Address])
    message.setRecipients(Message.RecipientType.BCC, bcc.toArray[Address])
    replyTo.foreach(address => message.setReplyTo(Array(address)))
    headers.foreach(header => message.setHeader(header.name, header.value))

    message
  }
}
