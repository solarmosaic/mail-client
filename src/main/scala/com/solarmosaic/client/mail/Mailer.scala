package com.solarmosaic.client.mail

import javax.mail.{Authenticator, Session, Transport}

import com.solarmosaic.client.mail.configuration.SmtpConfiguration

/**
 * SMTP mailer.
 *
 * @param config SMTP configuration for the mailer.
 * @param authenticator SMTP authentication for the mailer.
 */
case class Mailer(
  config: SmtpConfiguration,
  authenticator: Option[Authenticator] = None
) {
  lazy val session: Session = Session.getInstance(config.properties, authenticator.orNull)

  /** Sends an email message using the given [[Envelope]]. */
  def send(envelope: Envelope): Unit = Transport.send(envelope.message(session))
}
