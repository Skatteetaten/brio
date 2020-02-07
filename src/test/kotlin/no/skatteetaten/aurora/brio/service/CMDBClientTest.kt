package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import no.skatteetaten.aurora.brio.TestApp
import no.skatteetaten.aurora.brio.domain.Application
import no.skatteetaten.aurora.brio.domain.CmdbStatic
import no.skatteetaten.aurora.brio.domain.CmdbType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import kotlin.test.assertNotNull

@Import(CMDBClient::class)
@SpringBootTest(classes = [TestApp::class])
internal class CMDBClientTest {

    @Autowired
    lateinit var cmdbClient: CMDBClient

    @Test
    fun dummytest() {
    }

    @Test
    fun findObject() {
        val response1 = cmdbClient.findObjectOfTypeByName(CmdbType.Artifact, "artifact1")
        assertNotNull(response1)
        assertThat(response1.isEmpty).isFalse()
        assertThat(response1.getJSONObject(0).getString("Name")).isEqualTo("artifact1")
        val response2 = cmdbClient.findObjectOfTypeByArtifactIdAndGroupId(CmdbType.Artifact, "skattefinn-leveransepakke", "ske.arbeidsflate.skattefinn")
        assertNotNull(response2)
        assertThat(response2.isEmpty).isFalse()
        val response3 = cmdbClient.findObjectOfTypeByByArtifactIdAndGroupIdVersion(CmdbType.Artifact, "skattefinn-leveransepakke", "ske.arbeidsflate.skattefinn", "0.0.1")
        assertNotNull(response3)
        assertThat(response3.isEmpty).isFalse()
        println(response3[0].toString())

        val responseNone = cmdbClient.findObjectOfTypeByName(CmdbType.Artifact, "No_artifact")
        assertNotNull(responseNone)
        assertThat(responseNone.isEmpty).isTrue()
    }

    @Test
    fun findById() {
        val id = 69461
        val response = cmdbClient.findById(id)
        assertNotNull(response)
        assertThat(response.isEmpty).isFalse()
        assertThat(response.getInt(CmdbStatic.ID)).isEqualTo(id)
    }

    @Test
    fun createApplication() {
        val applicationNew = Application(null, null, "Brio", LocalDateTime.now(), LocalDateTime.now())
        val applicationExisting = Application(69501, "NOD1-69501", "skattefinn", LocalDateTime.now(), LocalDateTime.now())
        assertThat { cmdbClient.createObject(applicationExisting) }.isFailure()

        val createdApplication = cmdbClient.createObject(applicationNew)
        println(createdApplication)
        assertNotNull(createdApplication)
        cmdbClient.deleteObject(CmdbType.Application, createdApplication)
    }
}