package com.solarmosaic.client.mail

import java.util.Properties

case class Configuration(
  host: String,
  port: Int,
  tls: Boolean = false,
  debug: Boolean = false,
  authentication: Option[Credentials] = None
) {
  lazy val properties: Properties = {
    val properties = new Properties

    properties.put("mail.debug", debug.toString)
    properties.put("mail.smtp.host", host)
    properties.put("mail.smtp.port", port.toString)

    if (tls) {
      properties.put("mail.smtp.starttls.enable", "true")
      properties.put("mail.smtp.auth", "true")
    }

    properties
  }
}
