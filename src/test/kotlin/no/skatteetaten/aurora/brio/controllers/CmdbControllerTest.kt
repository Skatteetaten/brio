package no.skatteetaten.aurora.brio.controllers

import assertk.assertThat
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.AuroraMetrics
import no.skatteetaten.aurora.brio.TestApp
import no.skatteetaten.aurora.brio.security.CmdbSecretReader
import no.skatteetaten.aurora.brio.service.CmdbClient
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
        classes = [TestApp::class, CmdbClient::class, CmdbController::class, CmdbSecretReader::class]
)
internal class CmdbControllerTest: AbstractController() {

    @Autowired
    private lateinit var cmdbController: CmdbController

    @Test
    fun getCmdbInfo() {
        val result = cmdbController.getCmdbInfo()
        println(result)
        assertThat(result).isNotNull()
    }
}