package com.solarmosaic.client.mail

import javax.mail.Message.RecipientType
import javax.mail.internet.MimeMultipart

import com.solarmosaic.client.mail.configuration.SmtpConfiguration
import com.solarmosaic.client.mail.content.ContentType.MultipartTypes
import com.solarmosaic.client.mail.content._
import org.jvnet.mock_javamail.Mailbox
import org.specs2.specification.{AfterEach, Scope}

import scala.collection.convert.WrapAsScala

class MailerTests extends BaseSpec
  with AfterEach
  with EnvelopeWrappers
  with WrapAsScala {

  // Must be run sequentially because Mailbox persists state across tests.
  sequential

  /** Cleanup the Mailbox after each test completes. */
  def after = Mailbox.clearAll()

  "Mailer" should {
    trait MailerFixture extends Scope {
      val config = SmtpConfiguration("localhost", 25)
      val mailer = Mailer(config)
    }

    "fail for invalid envelope content" in new MailerFixture {
      val file = getFile("/test.txt")
      val envelope = Envelope(
        from = "from@test.com",
        to = Seq("to@test.com"),
        subject = "test",
        content = Attachment(file)
      )

      mailer.send(envelope) must throwAn[Exception]
    }

    "send a text-based email" in new MailerFixture {
      val envelope = Envelope(
        from = "from@test.com",
        to = Seq("to@test.com"),
        subject = "test",
        content = Text("text")
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getFrom.head must be equalTo envelope.from
      message.getAllRecipients.toSeq must be equalTo envelope.to
      message.getContent must be equalTo envelope.content.asInstanceOf[Text].text
      message.getSubject must be equalTo envelope.subject
    }

    "send an html-based email" in new MailerFixture {
      val envelope = Envelope(
        from = "from@example.com",
        to = Seq("to@example.com"),
        subject = "test",
        content = Html("<p>paragraph</p>")
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getFrom.head must be equalTo envelope.from
      message.getAllRecipients.toSeq must be equalTo envelope.to
      message.getContent must be equalTo envelope.content.asInstanceOf[Text].text
      message.getSubject must be equalTo envelope.subject
    }

    "send an alternative multipart email containing text and html" in new MailerFixture {
      val content = Multipart(
        parts = Seq(Text("text"), Html("<p>paragraph</p>")),
        subType = MultipartTypes.alternative
      )

      val envelope = Envelope(
        from = "from@example.com",
        to = Seq("to@example.com"),
        subject = "test",
        content = content
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getFrom.head must be equalTo envelope.from
      message.getAllRecipients.toSeq must be equalTo envelope.to
      message.getSubject must be equalTo envelope.subject

      val multipart = message.getContent.asInstanceOf[MimeMultipart]
      multipart.getCount must be equalTo content.parts.size
      multipart.getContentType must contain(content.contentType)

      content.parts.zipWithIndex.foreach { case (part, index) =>
        multipart.getBodyPart(index).getContent must be equalTo content.parts(index).part.getContent
      }
    }

    "send an alternative multipart email containing text and html and the given content headers" in new MailerFixture {
      val content = Multipart(
        parts = Seq(
          Text("text", headers = List(Header("X-Foo", "foo"))),
          Html("<p>paragraph</p>", headers = List(Header("X-Bar", "bar"), Header("X-Bar", "baz")))
        ),
        subType = MultipartTypes.alternative
      )

      val envelope = Envelope(
        from = "from@example.com",
        to = Seq("to@example.com"),
        subject = "test",
        content = content
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getFrom.head must be equalTo envelope.from
      message.getAllRecipients.toSeq must be equalTo envelope.to
      message.getSubject must be equalTo envelope.subject

      val multipart = message.getContent.asInstanceOf[MimeMultipart]
      multipart.getCount must be equalTo content.parts.size
      multipart.getContentType must contain(content.contentType)

      multipart.getBodyPart(0).getHeader("X-Foo") must be equalTo Array("foo")
      multipart.getBodyPart(1).getHeader("X-Bar") must be equalTo Array("bar", "baz")

      content.parts.zipWithIndex.foreach { case (part, index) =>
        multipart.getBodyPart(index).getContent must be equalTo content.parts(index).part.getContent
      }
    }

    "send a mixed multipart email containing text, html and file attachments" in new MailerFixture {
      val file = getFile("/test.txt")
      val content = Multipart(
        Text("text"),
        Html("<p>paragraph</p>"),
        Attachment(file, filename = Some("file1")),
        Attachment(file, filename = Some("file2"))
      )

      val envelope = Envelope(
        from = "from@example.com",
        to = Seq("to@example.com"),
        subject = "test",
        content = content
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getFrom.head must be equalTo envelope.from
      message.getAllRecipients.toSeq must be equalTo envelope.to
      message.getSubject must be equalTo envelope.subject

      val multipart = message.getContent.asInstanceOf[MimeMultipart]
      multipart.getCount must be equalTo content.parts.size
      multipart.getContentType must contain(content.contentType)
      content.parts.zipWithIndex.foreach { case (part, index) =>
        multipart.getBodyPart(index).getContent must be equalTo content.parts(index).part.getContent
      }
    }

    "send an email to the proper recipients" in new MailerFixture {
      val envelope = Envelope(
        from = "from@test.com",
        to = Seq("to@test.com"),
        subject = "test",
        content = "text",
        cc = Seq("cc1@test.com", "cc2@test.com"),
        bcc = Seq("bcc@test.com")
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getRecipients(RecipientType.TO).toSeq must be equalTo envelope.to
      message.getRecipients(RecipientType.CC).toSeq must be equalTo envelope.cc
      message.getRecipients(RecipientType.BCC).toSeq must be equalTo envelope.bcc
    }

    "send an email with the given reply-to addresses" in new MailerFixture {
      val envelope = Envelope(
        from = "from@test.com",
        to = Seq("to@test.com"),
        subject = "test",
        content = "text",
        replyTo = Seq("reply1@test.com", "reply2@test.com")
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getReplyTo.toSeq must be equalTo envelope.replyTo
    }

    "send an email with the given headers" in new MailerFixture {
      val envelope = Envelope(
        from = "from@test.com",
        to = Seq("to@test.com"),
        subject = "test",
        content = "text",
        headers = List(
          Header("X-Test-Foo", "foo"),
          Header("X-Test-Bar", "bar"),
          Header("X-Test-Foo", "foobar")
        )
      )

      mailer.send(envelope)

      val inbox = Mailbox.get(envelope.to.head)
      inbox.size must be equalTo 1

      val message = inbox.get(0)
      message.getHeader("X-Test-Foo") must be equalTo Array("foo", "foobar")
      message.getHeader("X-Test-Bar") must be equalTo Array("bar")
    }
  }
}
