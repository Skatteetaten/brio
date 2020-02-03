package no.skatteetaten.aurora.brio.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import assertk.assertThat
import assertk.assertions.*
import no.skatteetaten.aurora.brio.domain.Application
import no.skatteetaten.aurora.brio.domain.CmdbStatic
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

        val responseNone = service.findObjectOfTypeByName(CmdbType.Artifact, "No_artifact")
        assertNotNull(responseNone)
        assertThat(responseNone.isEmpty).isTrue()
    }

    @Test
    fun findById(){
        val id = 69461
        val response = service.findById(id)
        assertNotNull(response)
        assertThat(response.isEmpty).isFalse()
        assertThat(response.getInt(CmdbStatic.ID)).isEqualTo(id)
    }

    @Test
    fun createApplication() {
        val applicationNew = Application(null,null, "Brio", LocalDateTime.now(), LocalDateTime.now())
        val applicationExisting = Application(69501,"NOD1-69501", "skattefinn", LocalDateTime.now(), LocalDateTime.now())
        assertThat{service.createObject(applicationExisting)}.isFailure()

        val createdApplication = service.createObject(applicationNew)
        println(createdApplication)
        assertNotNull(createdApplication)
        service.deleteObject(CmdbType.Application, createdApplication)
    }
}