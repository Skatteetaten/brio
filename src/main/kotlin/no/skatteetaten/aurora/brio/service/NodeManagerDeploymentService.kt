package no.skatteetaten.aurora.brio.service

import mu.KotlinLogging
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NodeManagerDeploymentService {

    @Autowired
    lateinit var cmdbClient: CMDBClient
    @Autowired
    lateinit var cmdbObjectBuilder: CmdbObjectBuilder

    fun newNodeManagerDeployment(deployment: NodeManagerDeployment): NodeManagerDeployment? {

        return updateOrCreateObject(deployment)
    }



    private fun updateOrCreateObject(instance: NodeManagerDeployment): NodeManagerDeployment {
        val nmdJsonArray = cmdbClient.findObjectOfTypeByName(instance.objectType, instance.name)
        lateinit var newInstance : BaseCMDBObject
        if(nmdJsonArray.isEmpty()){
            //Create
            val jsonResponse = cmdbClient.createObject(instance.objectType, instance.toMinimalJson())
            newInstance = cmdbObjectBuilder.construct(jsonResponse)
        }else {
            //TODO: Check assumption to use first found instance if multiple instances found
            newInstance = cmdbObjectBuilder.construct(nmdJsonArray.getJSONObject(0))
        }
        return newInstance as NodeManagerDeployment
    }
}
