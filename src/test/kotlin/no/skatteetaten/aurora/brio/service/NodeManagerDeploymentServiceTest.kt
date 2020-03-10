package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.brio.domain.*
import no.skatteetaten.aurora.brio.security.CmdbSecretReader
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

internal class NodeManagerDeploymentServiceTest {
    private val server = MockWebServer()

    var cmdbClient = CmdbClient(RestTemplate(), CmdbSecretReader(null, "1234"), server.url("/").toString())
    var service = NodeManagerDeploymentService(cmdbClient)

    @Test
    fun newNodeManagerDeployment_Simple() {
        val nodemanagerResponse = CMDBMockUtil.nodeManager(1, "TestDeployment", null, null, null, null)
        // find by name - non existing
        server.enqueue(CMDBMockUtil.empty)
        // Create new
        server.enqueue(CMDBMockUtil.createObject(1, "TestDeployment"))
        // Find by id nyewly created, 3 times
        repeat(3) {
            server.enqueue(nodemanagerResponse)
        }
        server.enqueue(MockResponse().setResponseCode(404))

        server.execute() {
            val deployment = NodeManagerDeployment("TestDeployment")
            val result = service.newNodeManagerDeployment(deployment)
            assertNotNull(result)

            val result2 = service.newNodeManagerDeployment(deployment)
            assertNotNull(result)
            assertThat(result2).isEqualTo(result)
        }
    }

    @Test
    fun newNodeManagerDeployment_Full() {
        val deployment = NodeManagerDeployment("TestDeployment")
        deployment.applications = arrayOf(Application("TestApp"))
        deployment.artifacts = arrayOf(Artifact("TestArtifact", "no.jfw.test", "0.0.1", "TestArtifact"))
        deployment.databases = arrayOf(Database("MyDBTest"),
                Database("MyDBTest2"))
        deployment.applicationInstances = arrayOf(ApplicationInstance("TestApplicationInstance",
                Server("TestServer"),
                Environment("TestEnv", BusinessGroup("TestBG"))))

        // find new application - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new application
        server.enqueue((CMDBMockUtil.createObject(1, "TestApp")))
        // find new artifact - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new artifact
        server.enqueue((CMDBMockUtil.createObject(2, "TestArtifact")))
        // find new db - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new db
        server.enqueue((CMDBMockUtil.createObject(3, "TestDB1")))
        // find new db - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new db
        server.enqueue((CMDBMockUtil.createObject(4, "TestDB2")))
        // find new applicationInstance - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new applicationInstance
        server.enqueue((CMDBMockUtil.createObject(5, "TestApplicationInstance")))
        // find new server - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new server
        server.enqueue((CMDBMockUtil.createObject(6, "TestServer")))
        // find new nodeManager - empty
        server.enqueue(CMDBMockUtil.empty)
        // create new NodeManager
        server.enqueue(CMDBMockUtil.createObject(1, "TestDeployment"))
        // Find nodemanager
        repeat(3) {
            server.enqueue(CMDBMockUtil.nodeManager(1, "TestDeployment", deployment.applications, deployment.applicationInstances, deployment.artifacts, deployment.databases))
        }
        server.enqueue(MockResponse().setResponseCode(404))

        val result = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)
        if (result != null) {
            assertThat(result.applications).isNotNull()
            assertThat(result.artifacts).isNotNull()
            assertThat(result.databases).isNotNull()
            assertThat(result.applicationInstances).isNotNull()
            assertThat(result.applicationInstances!![0].runningOn).isNotNull()
            assertThat(result.applicationInstances!![0].environment).isNotNull()
        }
    }
}