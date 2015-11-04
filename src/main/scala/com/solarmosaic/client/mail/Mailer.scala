package com.solarmosaic.client.mail

import javax.mail.{Session, Transport}

import com.solarmosaic.client.mail.configuration.SmtpConfiguration

/**
 * SMTP mailer.
 *
 * @param config SMTP configuration for the mailer.
 */
case class Mailer(config: SmtpConfiguration) {
  lazy val session: Session = Session.getInstance(config.properties, config.authenticator.orNull)

  /** Sends an email message using the given [[Envelope]]. */
  def send(envelope: Envelope): Unit = Transport.send(envelope.message(session))
}
