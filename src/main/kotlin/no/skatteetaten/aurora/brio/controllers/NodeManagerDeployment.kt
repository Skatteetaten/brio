package no.skatteetaten.aurora.brio.controllers

import mu.KotlinLogging
import no.skatteetaten.aurora.AuroraMetrics
import no.skatteetaten.aurora.brio.service.NodeManagerDeploymentService
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate


private val logger = KotlinLogging.logger {}

/*
 * An example controller that shows how to do a REST call and how to do an operation with a operations metrics
 * There should be a metric called http_client_requests http_server_requests and operations
 */
@RestController
class NodeManagerDeployment(
    /*private val restTemplate: RestTemplate,
    private val metrics: AuroraMetrics,
    private val nodeManagerDeploymentService: NodeManagerDeploymentService*/
) {

    @PostMapping("/api/nodeManagerApplicationDeployment")
    fun createApplicationDeployment(@RequestBody nodeManagerPayload: String): String{
        logger.info("Creating new NodeManager deployment with payload:+n${nodeManagerPayload}")
        return "Received"
    }

    companion object {
        private val METRIC_NAME = "nodeManagerApplicationDeployment"
    }
}
