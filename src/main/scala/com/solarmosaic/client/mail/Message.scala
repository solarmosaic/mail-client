package com.solarmosaic.client.mail

import java.io.File
import javax.mail.internet.{MimeMultipart, MimeBodyPart}

case class MessageHeader(name: String, value: String)

case class MessagePart(
  payload: Any,
  contentType: BodyPartContentType,
  headers: List[MessageHeader] = List.empty[MessageHeader]
) {
  def body: MimeBodyPart = {
    val body = new MimeBodyPart
    headers.foreach(header => body.addHeader(header.name, header.value))
    payload match {
      case file: File => body.attachFile(file)
      case content => body.setContent(content, contentType.toString)
    }
    body
  }
}

class MessageParts(
  parts: Seq[MessagePart],
  contentType: Option[MultipartContentType] = None
) {
  def content: MimeMultipart = {
    val multipart = new MimeMultipart
    contentType.foreach(ct => multipart.setSubType(ct.subType))
    parts.foreach(part => multipart.addBodyPart(part.body))
    multipart
  }
}
