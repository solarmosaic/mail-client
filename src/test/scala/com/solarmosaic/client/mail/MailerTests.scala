package com.solarmosaic.client.mail

import com.solarmosaic.client.mail.configuration.SmtpConfiguration
import com.solarmosaic.client.mail.message.{EnvelopeWrappers, Envelope}
import org.jvnet.mock_javamail.Mailbox
import org.specs2.concurrent.ExecutionEnv

class MailerTests(implicit ee: ExecutionEnv) extends BaseSpec with EnvelopeWrappers {
  "Mailer" should {
    "send an email" in {
      val config = SmtpConfiguration("localhost", 2525)
      val mailer = Mailer(config)
      val content = "this is a test"
      val envelope = Envelope(
        from = "from@example.com",
        to = Seq("to@example.com"),
        subject = "test",
        content = content
      )
      mailer.send(envelope).map { result =>
        result must be equalTo Unit
      }.await

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getContent must be equalTo content
      message.getSubject must be equalTo envelope.subject
    }
  }
}
