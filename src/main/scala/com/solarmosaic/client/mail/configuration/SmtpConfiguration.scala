package com.solarmosaic.client.mail.configuration

import java.util.Properties

case class SmtpConfiguration(
  host: String,
  port: Int,
  tls: Boolean = false,
  debug: Boolean = false
) {
  lazy val properties: Properties = {
    val props = new Properties

    props.put("mail.debug", debug.toString)
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.port", port.toString)

    if (tls) {
      props.put("mail.smtp.starttls.enable", "true")
      props.put("mail.smtp.auth", "true")
    }

    props
  }
}
