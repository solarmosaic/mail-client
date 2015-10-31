package com.solarmosaic.client.mail

import javax.mail.{Transport, Session}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Mailer(config: Configuration) {
  lazy val session: Session = Session.getInstance(config.properties, config.authentication.orNull)

  def send(envelope: Envelope): Future[Unit] = Future(Transport.send(envelope.createMessage(session)))
}
