package com.solarmosaic.client.mail.configuration

import javax.mail.{Authenticator, PasswordAuthentication}

/**
 * Provides simple password authentication for the SMTP request.
 *
 * @param username The username
 * @param password The password
 */
case class PasswordAuthenticator(
  username: String,
  password: String
) extends Authenticator {

  /** Provides the `PasswordAuthentication` object to the `Authenticator`. */
  override def getPasswordAuthentication: PasswordAuthentication = new PasswordAuthentication(username, password)
}
