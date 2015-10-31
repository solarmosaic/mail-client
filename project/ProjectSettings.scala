object ProjectSettings {
  def projectRootName = "mail-client"

  object version {
    val scala210 = "2.10.4"
    val scala211 = "2.11.7"
    val scalaCrossBuild = Seq(scala210, scala211)
    val specs2 = "3.6.5"
  }
}
