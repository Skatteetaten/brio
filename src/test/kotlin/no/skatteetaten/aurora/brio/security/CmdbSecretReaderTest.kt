package no.skatteetaten.aurora.brio.security

import assertk.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class CmdbSecretReaderTest {

    @Test
    fun getSecret_withValue() {
        val cmdbSecretReader = CmdbSecretReader(null, "token")
        assertThat(cmdbSecretReader.secret).equals("token")
    }

    @Test
    fun getSecret_fromFile() {
        val secretFile = File("build/tmp/cmdbSecret")
        secretFile.writeText("token_file")
        val cmdbSecretReader = CmdbSecretReader(secretFile.absolutePath, null)
        assertThat(cmdbSecretReader.secret).equals("token_file")
        secretFile.delete()
    }
}