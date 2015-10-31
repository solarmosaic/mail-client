package com.solarmosaic.client.mail

import java.io.File

import org.specs2.mock.Mockito
import org.specs2.mock.mockito.MockitoFunctions
import org.specs2.mutable

class BaseSpec
  extends mutable.Specification
  with Mockito
  with MockitoFunctions {

  /** Get a resource from the classpath as a [[File]]. */
  def getFile(resource: String): File = {
    new File(getClass.getResource(resource).getFile)
  }
}
