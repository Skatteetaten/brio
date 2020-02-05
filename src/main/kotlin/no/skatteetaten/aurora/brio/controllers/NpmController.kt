package no.skatteetaten.aurora.brio.controllers

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

/*
 * An example controller that shows how to do a REST call and how to do an operation with a operations metrics
 * There should be a metric called http_client_requests http_server_requests and operations
 */
@RestController
@RequestMapping("/api")
class NodeManagerDeployment {
    @GetMapping("/info")
    fun getInfo(): String = "Some Info"

    @PostMapping("/nodeManagerApplicationDeployment")
    fun createApplicationDeployment(@RequestBody nodeManagerPayload: String): String {
        logger.info("Creating new NodeManager deployment with payload:\n$nodeManagerPayload")
        return "Received"
    }

    companion object {
        private const val METRIC_NAME = "nodeManagerApplicationDeployment"
    }
}
