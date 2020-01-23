package no.skatteetaten.aurora.brio.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import assertk.assertThat
import assertk.assertions.*
import no.skatteetaten.aurora.brio.domain.Application
import no.skatteetaten.aurora.brio.domain.CmdbType
import java.time.LocalDateTime
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@Import(CMDBClient::class)
internal class CMDBClientTest {

    @Autowired
    lateinit var service: CMDBClient


    @Test
    fun findObject() {
        val response1 = service.findObjectOfTypeByName(CmdbType.Artifact, "artifact1")
        assertNotNull(response1)
        assertThat(response1.isEmpty).isFalse()
        assertThat(response1.getJSONObject(0).getString("Name")).isEqualTo("artifact1")
        val response2 = service.findObjectOfTypeByArtifactIdAndGroupId(CmdbType.Artifact, "skattefinn-leveransepakke", "ske.arbeidsflate.skattefinn")
        assertNotNull(response2)
        assertThat(response2.isEmpty).isFalse()
        val response3 = service.findObjectOfTypeByByArtifactIdAndGroupIdVersion(CmdbType.Artifact, "skattefinn-leveransepakke", "ske.arbeidsflate.skattefinn", "0.0.1")
        assertNotNull(response3)
        assertThat(response3.isEmpty).isFalse()
        println(response3[0].toString())

        val response_none = service.findObjectOfTypeByName(CmdbType.Artifact, "No_artifact")
        assertNotNull(response_none)
        assertThat(response_none.isEmpty).isTrue()
    }

    @Test
    fun createApplication() {
        val application_new = Application(null,null, "Brio", LocalDateTime.now(), LocalDateTime.now())
        val application_existing = Application(69501,"NOD1-69501", "skattefinn", LocalDateTime.now(), LocalDateTime.now())
        assertThat{service.createObject(application_existing)}.isFailure()

        val createdApplication = service.createObject(application_new)
        println(createdApplication)
        assertNotNull(createdApplication)
        service.deleteObject(CmdbType.Application, createdApplication)
    }
}