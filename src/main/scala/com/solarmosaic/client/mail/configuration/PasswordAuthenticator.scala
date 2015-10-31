package com.solarmosaic.client.mail.configuration

import javax.mail.{Authenticator, PasswordAuthentication}

case class PasswordAuthenticator(
  username: String,
  password: String
) extends Authenticator {
  override def getPasswordAuthentication: PasswordAuthentication = new PasswordAuthentication(username, password)
}
