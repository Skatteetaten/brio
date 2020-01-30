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

    val dateFormatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")

    fun construct(jsonString: String): BaseCMDBObject {
        val node = JSONObject(jsonString)
        return construct(node)
    }

    private val OBJECT_TYPE = "objectType"
    private val ID = "id"
    private val KEY = "Key"
    private val NAME = "Name"
    private val CREATED = "Created"
    private val UPDATED = "Updated"
    private val PART_OF = "PartOf"
    private val APPLICATIONS = "Applications"
    private val VERSION = "Version"
    private val ARTIFACT_ID = "ArtifactID"
    private val ATTRIBUTE = "attribute"
    private val GROUP_ID = "GroupId"
    private val ARTIFACTS = "Artifacts"
    private val DATABASES = "Databases"
    private val OBJECT_KEY = "objectKey"
    private val APPLICATION_INSTANCES = "ApplicationInstances"

    private val TYPE_ARTIFACT = "Artifact"
    private val TYPE_APPLICATION_INSTANCE = "ApplicationInstance"
    private val TYPE_DATABASE = "Database"
    private val TYPE_APPLICATION = "Application"
    private val TYPE_NODE_MANAGER_DEPLOYMENT = "NodeManagerDeployment"




    fun construct(node: JSONObject): BaseCMDBObject {
        val objectType = node.getString(OBJECT_TYPE)

        val id : Int? = if(node.has(ID)) node.getInt(ID) else null
        val key : String? = if(node.has(KEY)) node.getString(KEY) else null
        val name = node.getString(NAME)
        val created = LocalDateTime.parse(node.getString(CREATED), dateFormatter)
        val updated = LocalDateTime.parse(node.getString(UPDATED), dateFormatter)

        lateinit var cmdbObject: BaseCMDBObject
        when (objectType) {
            TYPE_ARTIFACT -> cmdbObject = Artifact(
                    id, key, name, created, updated,
                    if(node.has(PART_OF) && node.getString(PART_OF) != "") constructChildNode(node.getJSONObject(PART_OF)) as Application? else null,
                    node.getString(GROUP_ID),
                    node.getString(VERSION),
                    node.getString(ARTIFACT_ID)
            )
            TYPE_APPLICATION_INSTANCE -> {
                val server = if(hasValue(node, CmdbStatic.RUNNING_ON))
                    constructChildNode(node.getJSONObject(CmdbStatic.RUNNING_ON)) as Server else null
                val environment = if(hasValue(node, CmdbStatic.ENVIRONMENT))
                    constructChildNode(node.getJSONObject(CmdbStatic.ENVIRONMENT)) as Environment else null

                cmdbObject = ApplicationInstance(id, key, name, created, updated, server, environment)
            }
            TYPE_DATABASE -> cmdbObject = Database(id, key, name, created, updated)
            TYPE_APPLICATION -> cmdbObject = Application(id, key, name, created, updated)
            CmdbStatic.TYPE_SERVER -> cmdbObject = Server(id, key, name, created, updated)
            CmdbStatic.TYPE_ENVIRONMENT -> {
                val businessGroup = if(hasValue(node, CmdbStatic.BUSINESSGROUP))
                    constructChildNode(node.getJSONObject(CmdbStatic.BUSINESSGROUP)) as BusinessGroup else null
                cmdbObject = Environment(id, key, name, created, updated, businessGroup)
            } //TODO Implement business groupt
            CmdbStatic.TYPE_BUSINESSGROUP -> cmdbObject = BusinessGroup(id, key, name, created, updated)
            TYPE_NODE_MANAGER_DEPLOYMENT -> {
                val applications= if(node.has(APPLICATIONS) && node.get(APPLICATIONS) != "")
                    getJsonObjectOrArray(node.get(APPLICATIONS)) as MutableList<Application> else ArrayList()
                val artifacts = if(node.has(ARTIFACTS) && node.get(ARTIFACTS) != "")
                    getJsonObjectOrArray(node.get(ARTIFACTS)) as MutableList<Artifact> else ArrayList()
                val databases = if(node.has(DATABASES)  && node.get(DATABASES) != "")
                    getJsonObjectOrArray(node.get(DATABASES)) as MutableList<Database> else ArrayList()
                val applicationInstances = if(node.has(APPLICATION_INSTANCES)  && node.get(APPLICATION_INSTANCES) != "")
                    getJsonObjectOrArray(node.get(APPLICATION_INSTANCES)) as MutableList<ApplicationInstance> else ArrayList()

                cmdbObject = NodeManagerDeployment(id, key, name, created, updated, applications, artifacts, databases, applicationInstances)
            }
        }
        return cmdbObject
    }

    fun constructChildNode(node: JSONObject) : BaseCMDBObject?{
        if(hasValue(node, CmdbStatic.OBJECT_KEY)) {
            val key = node.getString(OBJECT_KEY)
            val child = cmdbClient.findByKey(key) ?: return null
            if (child.isEmpty) return null
            return construct(child)
        }else{
            return null
        }
    }

    /**If only one instance is present in CMDB, it is returned as single JSONObject, while with more than one instance it is returned as JSONArray*/
    private fun getJsonObjectOrArray(node: Any) : MutableList<BaseCMDBObject> {
        when(node.javaClass){
            JSONObject::class.java -> {
                val list = ArrayList<BaseCMDBObject>()
                constructChildNode(node as JSONObject)?.let { list.add(it) }
                return list
            }
            JSONArray::class.java -> return (node as JSONArray).map { constructChildNode(it as JSONObject) as BaseCMDBObject }.toMutableList()

        }
        return ArrayList()
    }

    private fun hasValue(node: JSONObject, propertyName: String) : Boolean{
        return node.has(propertyName) && node.get(propertyName) != ""
    }
}