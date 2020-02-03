package no.skatteetaten.aurora.brio.service

import no.skatteetaten.aurora.brio.domain.*
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CmdbObjectBuilder {
    @Autowired
    lateinit var cmdbClient: CMDBClient

    constructor(cmdbClient: CMDBClient){
        this.cmdbClient = cmdbClient
    }

    val dateFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")

    fun buildCmdObject(jsonString: String): BaseCMDBObject {
        val node = JSONObject(jsonString)
        return buildCmdObject(node)
    }


    fun buildCmdObject(node: JSONObject): BaseCMDBObject {
        val objectType = node.getString(CmdbStatic.OBJECT_TYPE)

        val id : Int? = if(node.has(CmdbStatic.ID)) node.getInt(CmdbStatic.ID) else null
        val key : String? = if(node.has(CmdbStatic.KEY)) node.getString(CmdbStatic.KEY) else null
        val name = node.getString(CmdbStatic.NAME)
        val created = LocalDateTime.parse(node.getString(CmdbStatic.CREATED), dateFormatter)
        val updated = LocalDateTime.parse(node.getString(CmdbStatic.UPDATED), dateFormatter)

        lateinit var cmdbObject: BaseCMDBObject
        when (objectType) {
            CmdbStatic.TYPE_ARTIFACT -> cmdbObject = Artifact(
                    id, key, name, created, updated,
                    if(node.has(CmdbStatic.PART_OF) && node.get(CmdbStatic.PART_OF) != "")
                        constructChildNode(node.getJSONObject(CmdbStatic.PART_OF)) as Application? else null,
                    node.getString(CmdbStatic.GROUP_ID),
                    node.getString(CmdbStatic.VERSION),
                    node.getString(CmdbStatic.ARTIFACT_ID)
            )
            CmdbStatic.TYPE_APPLICATION_INSTANCE -> {
                val server = if(hasValue(node, CmdbStatic.RUNNING_ON))
                    constructChildNode(node.getJSONObject(CmdbStatic.RUNNING_ON)) as Server else null
                val environment = if(hasValue(node, CmdbStatic.ENVIRONMENT))
                    constructChildNode(node.getJSONObject(CmdbStatic.ENVIRONMENT)) as Environment else null

                cmdbObject = ApplicationInstance(id, key, name, created, updated, server, environment)
            }
            CmdbStatic.TYPE_DATABASE -> cmdbObject = Database(id, key, name, created, updated)
            CmdbStatic.TYPE_APPLICATION -> cmdbObject = Application(id, key, name, created, updated)
            CmdbStatic.TYPE_SERVER -> cmdbObject = Server(id, key, name, created, updated)
            CmdbStatic.TYPE_ENVIRONMENT -> {
                val businessGroup = if(hasValue(node, CmdbStatic.BUSINESSGROUP))
                    constructChildNode(node.getJSONObject(CmdbStatic.BUSINESSGROUP)) as BusinessGroup else null
                cmdbObject = Environment(id, key, name, created, updated, businessGroup)
            } //TODO Implement business groupt
            CmdbStatic.TYPE_BUSINESSGROUP -> cmdbObject = BusinessGroup(id, key, name, created, updated)
            CmdbStatic.TYPE_NODE_MANAGER_DEPLOYMENT -> {
                val applications= if(node.has(CmdbStatic.APPLICATIONS) && node.get(CmdbStatic.APPLICATIONS) != "")
                    getJsonObjectOrArray(node.get(CmdbStatic.APPLICATIONS)) as MutableList<Application> else ArrayList()
                val artifacts = if(node.has(CmdbStatic.ARTIFACTS) && node.get(CmdbStatic.ARTIFACTS) != "")
                    getJsonObjectOrArray(node.get(CmdbStatic.ARTIFACTS)) as MutableList<Artifact> else ArrayList()
                val databases = if(node.has(CmdbStatic.DATABASES)  && node.get(CmdbStatic.DATABASES) != "")
                    getJsonObjectOrArray(node.get(CmdbStatic.DATABASES)) as MutableList<Database> else ArrayList()
                val applicationInstances = if(node.has(CmdbStatic.APPLICATION_INSTANCES)  && node.get(CmdbStatic.APPLICATION_INSTANCES) != "")
                    getJsonObjectOrArray(node.get(CmdbStatic.APPLICATION_INSTANCES)) as MutableList<ApplicationInstance> else ArrayList()

                cmdbObject = NodeManagerDeployment(id, key, name, created, updated, applications, artifacts, databases, applicationInstances)
            }
        }
        return cmdbObject
    }

    fun constructChildNode(node: JSONObject) : BaseCMDBObject?{
        if(hasValue(node, CmdbStatic.OBJECT_KEY)) {
            val key = node.getString(CmdbStatic.OBJECT_KEY)
            val child = cmdbClient.findByKey(key) ?: return null
            if (child.isEmpty) return null
            return buildCmdObject(child)
        }else{
            return null
        }
    }

    /**If only one instance is present in CMDB, it is returned as single JSONObject, while with more than one instance it is returned as JSONArray*/
    private fun getJsonObjectOrArray(node: Any) : MutableList<BaseCMDBObject> {
        val list = ArrayList<BaseCMDBObject>()
        when(node.javaClass){
            JSONObject::class.java -> {
                constructChildNode(node as JSONObject)?.let { list.add(it) }
            }
            JSONArray::class.java -> {
                (node as JSONArray).forEach {
                    val child = constructChildNode(it as JSONObject)
                    if(child != null) list.add(child)
                }
            }

        }
        return list
    }

    private fun hasValue(node: JSONObject, propertyName: String) : Boolean{
        return node.has(propertyName) && node.get(propertyName) != ""
    }
}