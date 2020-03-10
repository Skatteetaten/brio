package no.skatteetaten.aurora.brio.controllers

import no.skatteetaten.aurora.brio.service.CmdbClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cmdb")
class CmdbController(private val cmdbClient: CmdbClient) {

    @GetMapping("/info")
    fun getCmdbInfo(): String {
        val schemaInfo = cmdbClient.getSchemaInfo()
        val types = cmdbClient.getTypes()

        return "CMDB Info:\nInfo:\n$schemaInfo\n------\nTypes:\n$types"
    }
}