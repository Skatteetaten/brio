package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.brio.TestApp
import no.skatteetaten.aurora.brio.domain.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(classes = [TestApp::class])
@Import(NodeManagerDeploymentService::class, CMDBClient::class, CmdbObjectBuilder::class)
internal class NodeManagerDeploymentServiceTest {

    @Autowired
    lateinit var service: NodeManagerDeploymentService

    @Test
    fun dummytest() {
    }

    @Test
    fun newNodeManagerDeployment_Simple() {
        val deployment = NodeManagerDeployment("TestDeployment")
        val result = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)

        val result2 = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)
        assertThat(result2).isEqualTo(result)

        if (result != null) service.deleteNpmObject(result)
    }

    @Test
    fun newNodeManagerDeployment_Full() {
        val deployment = NodeManagerDeployment("TestDeployment")
        deployment.applications.add(Application("TestApp"))
        deployment.artifacts.add((Artifact("TestArtifact", "no.jfw.test", "0.0.1", "TestArtifact")))
        deployment.databases.add((Database("MyDBTest")))
        deployment.databases.add((Database("MyDBTest2")))
        deployment.applicationInstances.add(ApplicationInstance("TestApplicationInstance",
                Server("TestServer"),
                Environment("TestEnv", BusinessGroup("TestBG"))))

        val result = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)
        if (result != null) {
            assertThat(result.applications).isNotEmpty()
            assertThat(result.artifacts).isNotEmpty()
            assertThat(result.databases).isNotEmpty()
            assertThat(result.applicationInstances).isNotEmpty()
            assertThat(result.applicationInstances[0].runningOn).isNotNull()
            assertThat(result.applicationInstances[0].environment).isNotNull()
        }

        val result2 = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)
        assertThat(result2).isEqualTo(result)

        if (result != null) {
            result.applicationInstances.forEach {
                val runningOn = it.runningOn
                if (runningOn != null) service.deleteNpmObject(runningOn)
                val environment = it.environment
                if (environment != null) {
                    val businessGroup = environment.businessGroup
                    if (businessGroup != null) service.deleteNpmObject(businessGroup)
                    service.deleteNpmObject(environment)
                }
                service.deleteNpmObject(it)
            }
            result.applications.forEach { service.deleteNpmObject(it) }
            result.databases.forEach { service.deleteNpmObject(it) }
            result.artifacts.forEach { service.deleteNpmObject(it) }
            service.deleteNpmObject(result)
        }
    }
}