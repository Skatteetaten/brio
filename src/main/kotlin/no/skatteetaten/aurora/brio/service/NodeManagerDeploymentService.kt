package no.skatteetaten.aurora.brio.service

import mu.KotlinLogging
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import no.skatteetaten.aurora.brio.domain.CmdbStatic
import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
import org.json.JSONObject
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NodeManagerDeploymentService(private val cmdbClient: CmdbClient) {

    fun newNodeManagerDeployment(deployment: NodeManagerDeployment): NodeManagerDeployment? {
        logger.info("Deploying NodeManagerDeployment ${deployment.name}")
        deployment.applications?.forEach {
            updateOrCreateNamedObject(it)
        }

        deployment.databases?.forEach {
            updateOrCreateNamedObject(it)
        }

        deployment.artifacts?.forEach {
            updateOrCreateNamedObject(it)
        }

        deployment.applicationInstances?.forEach {
            val env = it.environment
            if (env != null) updateOrCreateNamedObject(env)
            val runningOn = it.runningOn
            if (runningOn != null) updateOrCreateNamedObject(runningOn)

            updateOrCreateNamedObject(it)
        }

        return updateOrCreateNodeManagerDeployment(deployment)
    }

    fun deleteNpmObject(instance: BaseCMDBObject): Boolean {
        return cmdbClient.deleteObject(instance)
    }

    private fun updateOrCreateNodeManagerDeployment(instance: NodeManagerDeployment): NodeManagerDeployment {
        val jsonInstance = updateOrCreateNamedObject(instance)
        // Need to retreive newly created by finBy iql as returned json after create is incomplete
        val id = jsonInstance.getInt(CmdbStatic.ID)
        val instance = cmdbClient.findObjectById(id) ?: throw Exception("Can not find newly created object in CMDB by id $id")
        return instance as NodeManagerDeployment
    }

    private fun updateOrCreateNamedObject(instance: BaseCMDBObject): JSONObject {
        val nmdJsonArray = cmdbClient.findObjectOfTypeByName(instance.objectType, instance.name)
        val newInstance = if (nmdJsonArray.isEmpty) {
            // Create
            cmdbClient.createObject(instance.objectType, instance.toMinimalJson())
        } else {
            // TODO: Check assumption to use first found instance if multiple instances found
            nmdJsonArray.getJSONObject(0)
        }
        instance.id = newInstance.getInt(CmdbStatic.ID)
        return newInstance
    }
}
