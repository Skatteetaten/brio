package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.brio.domain.Artifact
import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

internal class CMDBClientTest_mocked {

    private val server = MockWebServer()

    private val cmdbClient = CMDBClient(RestTemplate(), server.url("/").toString(), "1234")

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun findBlyId() {
        // given
        val artifactResponse = CMDBMockUtil.artifactResponse
        server.execute(artifactResponse) {
            val response = cmdbClient.findById(174067)
            assertThat(response).isNotNull()
            assertThat(response?.getInt("id")).isEqualTo(174067)
        }
    }

    @Test
    fun findObjectBlyId() {
        val artifactResponse = CMDBMockUtil.artifactResponse
        server.execute(artifactResponse) {
            val response = cmdbClient.findObjectById(174067)
            assertThat(response).isNotNull()
            assertThat(response?.name).isEqualTo("TestArtifact")
            if (response != null) assertThat(response).isInstanceOf(Artifact::class)
        }
    }

    @Test
    fun findNodeManagerDeployment() {
        val nodeManagerResponse = CMDBMockUtil.nodeManagerResponse
        server.execute(nodeManagerResponse) {
            val response = cmdbClient.findObjectById(69527)
            assertThat(response).isNotNull()
            assertThat(response?.name).isEqualTo("TestDeployment")
            if (response != null) assertThat(response).isInstanceOf(NodeManagerDeployment::class)
        }
    }
}