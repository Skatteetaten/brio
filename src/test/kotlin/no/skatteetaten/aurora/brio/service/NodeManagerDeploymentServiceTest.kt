package no.skatteetaten.aurora.brio.service

import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(NodeManagerDeploymentService::class, CMDBClient::class, CmdbObjectBuilder::class)
internal class NodeManagerDeploymentServiceTest {

    @Autowired
    lateinit var service: NodeManagerDeploymentService

    @Test
    fun newNodeManagerDeployment_Simple() {
        var deployment = NodeManagerDeployment(
                null, null,
                "TestDeployment",
                null, null,
                ArrayList(),
                ArrayList(),
                ArrayList(),
                ArrayList()
        )
        val result = service.newNodeManagerDeployment(deployment)
        assertNotNull(result)
    }
}