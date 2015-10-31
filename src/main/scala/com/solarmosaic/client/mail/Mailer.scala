package com.solarmosaic.client.mail

import javax.mail._

import com.solarmosaic.client.mail.configuration.SmtpConfiguration
import com.solarmosaic.client.mail.message.Envelope

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Mailer(config: SmtpConfiguration, authenticator: Option[Authenticator] = None) {
  lazy val session: Session = Session.getInstance(config.properties, authenticator.orNull)

  def send(envelope: Envelope): Future[Unit] = Future(Transport.send(envelope.message(session)))
}
