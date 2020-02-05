package no.skatteetaten.aurora.brio.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import no.skatteetaten.aurora.AuroraMetrics
import no.skatteetaten.aurora.brio.controllers.ErrorHandler
import no.skatteetaten.aurora.brio.controllers.NodeManagerDeployment
import no.skatteetaten.aurora.brio.service.NodeManagerDeploymentService
import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.get
import no.skatteetaten.aurora.mockmvc.extensions.statusIsOk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.client.MockRestServiceServer

class Config {
    @Bean
    fun meterRegistry(): MeterRegistry = Metrics.globalRegistry
}

@WebMvcTest(controllers = [NodeManagerDeployment::class, ErrorHandler::class])
@Import(value = [Config::class, AuroraMetrics::class])
@AutoConfigureWebClient(registerRestTemplate = true)
@AutoConfigureMockRestServiceServer
class NodeManagerDeploymentTest : AbstractController() {

    @Autowired
    private lateinit var server: MockRestServiceServer

    @MockBean
    private lateinit var nodeManagerDeploymentService: NodeManagerDeploymentService

    @Test
    fun `Test creation of new NodeManager deployment`() {
        val apiUrl = "/api/nodeManagerApplicationDeployment"
        val paylod = "This is a test payload"

        mvc.get(Path("/api/info", "")) {
            statusIsOk()
        }
    }
}
