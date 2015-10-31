package com.solarmosaic.client.mail

import javax.mail.{PasswordAuthentication, Authenticator}

case class Credentials(
  username: String,
  password: String
) extends Authenticator {
  override def getPasswordAuthentication: PasswordAuthentication = new PasswordAuthentication(username, password)
}
