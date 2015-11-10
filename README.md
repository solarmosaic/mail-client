# Scala SMTP Mail Client

A [Scala](http://scala-lang.org/) [SMTP](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol) mailer
built on top of the [JavaMail API](https://javamail.java.net/nonav/docs/api/).

## Installation

Releases are available in [Maven](http://repo1.maven.org/maven2/com/solarmosaic/client) for
[Scala 2.10](http://repo1.maven.org/maven2/com/solarmosaic/client/mail-client_2.10/) and
[Scala 2.11](http://repo1.maven.org/maven2/com/solarmosaic/client/mail-client_2.11/). If you're using
[sbt](http://www.scala-sbt.org/), simply add the following to your `build.sbt` file:

```scala
libraryDependencies += "com.solarmosaic.client" %% "mail-client" % "0.1.0"
```

## Usage

```scala
import com.solarmosaic.client.mail._
import com.solarmosaic.client.mail.configuration._
import com.solarmosaic.client.mail.content._

class ExampleClass extends EnvelopeWrappers {
  val config = SmtpConfiguration("localhost", 25)
  val mailer = Mailer(config)
  val content = Multipart(
    parts = Seq(Text("text"), Html("<p>text</p>")),
    subType = MultipartTypes.alternative
  )

  val envelope = Envelope(
    from = "from@example.com",
    to = Seq("to@example.com"),
    subject = "test",
    content = content
  )

  mailer.send(envelope)
}
```

For more detailed usage examples you may refer to the
[unit tests](https://github.com/solarmosaic/mail-client/blob/master/src/test/scala/com/solarmosaic/client/mail/MailerTests.scala)
inside this repository.

## Contributing

This repository is a mirror of our internal [Gerrit](https://www.gerritcodereview.com/) repository. Therefore, we do not
support merging code through Github itself. Please feel free to create issues or open pull requests here and we will do
our best to resolve them internally.

## Inspiration

This library was inspired by [courier](https://github.com/softprops/courier) and
[scala-mail](https://github.com/pascalmouret/scala-mail).

## License

[MIT](http://opensource.org/licenses/MIT)
