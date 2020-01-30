package no.skatteetaten.aurora.brio.service

import mu.KotlinLogging
import no.skatteetaten.aurora.brio.domain.*
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception

private val logger = KotlinLogging.logger {}

@Component
class NodeManagerDeploymentService {

    @Autowired
    lateinit var cmdbClient: CMDBClient
    @Autowired
    lateinit var cmdbObjectBuilder: CmdbObjectBuilder

    fun newNodeManagerDeployment(deployment: NodeManagerDeployment): NodeManagerDeployment? {
        deployment.applications.forEach{
            updateOrCreateNamedObject(it)
        }

        deployment.databases.forEach{
            updateOrCreateNamedObject(it)
        }

        deployment.artifacts.forEach {
            updateOrCreateNamedObject(it)
        }

        deployment.applicationInstances.forEach {
            val env = it.environment
            if(env != null) updateOrCreateNamedObject(env)
            val runningOn = it.runningOn
            if(runningOn != null) updateOrCreateNamedObject(runningOn)

            updateOrCreateNamedObject(it)
        }

        return updateOrCreateNodeManagerDeployment(deployment)
    }

    fun deleteNpmObject(instance: BaseCMDBObject) : Boolean{
        return cmdbClient.deleteObject(instance)
    }


    private fun updateOrCreateNodeManagerDeployment(instance: NodeManagerDeployment): NodeManagerDeployment {
        val jsonInstance = updateOrCreateNamedObject(instance)
        //Need to retreive newly created by finBy iql as returned json after create is incomplete
        val id = jsonInstance.getInt(CmdbStatic.ID)
        val newJsonInstance = cmdbClient.findById(id)
                ?: throw Exception("Can not find newly created object in CMDB by id $id")
        return cmdbObjectBuilder.construct(newJsonInstance) as NodeManagerDeployment
    }

    private fun updateOrCreateNamedObject(instance: BaseCMDBObject) : JSONObject{
        val nmdJsonArray = cmdbClient.findObjectOfTypeByName(instance.objectType, instance.name)
        lateinit var newInstance : JSONObject
        if(nmdJsonArray.isEmpty()){
            //Create
            newInstance = cmdbClient.createObject(instance.objectType, instance.toMinimalJson())
        }else {
            //TODO: Check assumption to use first found instance if multiple instances found
            newInstance = nmdJsonArray.getJSONObject(0)
        }
        instance.id = newInstance.getInt(CmdbStatic.ID)
        return newInstance
    }
}
