package no.skatteetaten.aurora.brio.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import no.skatteetaten.aurora.AuroraMetrics
import no.skatteetaten.aurora.brio.controllers.ErrorHandler
import no.skatteetaten.aurora.brio.controllers.NodeManagerDeployment
import no.skatteetaten.aurora.brio.service.NodeManagerDeploymentService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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

        mvc.perform(post(apiUrl)
                .content(paylod)
                .contentType(MediaType.TEXT_PLAIN)
        )
            .andExpect(status().isOk)
            /*.andDo(
                document(
                    "example-ip-get",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("ip")
                            .type(JsonFieldType.STRING)
                            .description("The ip of this service as seen from the Internet")
                    )
                )
            )*/
    }
}
