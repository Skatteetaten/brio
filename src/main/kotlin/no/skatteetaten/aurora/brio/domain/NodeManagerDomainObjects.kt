package no.skatteetaten.aurora.brio.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.json.JSONObject
import java.time.LocalDateTime


object CmdbStatic{
    const val OBJECT_TYPE = "objectType"
    const val ID = "id"
    const val KEY = "Key"
    const val NAME = "Name"
    const val CREATED = "Created"
    const val UPDATED = "Updated"
    const val PART_OF = "PartOf"
    const val APPLICATIONS = "Applications"
    const val VERSION = "Version"
    const val ARTIFACT_ID = "ArtifactID"
    const val ATTRIBUTE = "attribute"
    const val GROUP_ID = "GroupId"
    const val ARTIFACTS = "Artifacts"
    const val DATABASES = "Databases"
    const val OBJECT_KEY = "objectKey"
    const val APPLICATION_INSTANCES = "ApplicationInstances"
    const val RUNNING_ON = "RunningOn"
    const val ENVIRONMENT = "Environment"
    const val BUSINESSGROUP = "BusinessGroup"

    const val TYPE_ARTIFACT = "Artifact"
    const val TYPE_APPLICATION_INSTANCE = "ApplicationInstance"
    const val TYPE_DATABASE = "Database"
    const val TYPE_APPLICATION = "Application"
    const val TYPE_NODE_MANAGER_DEPLOYMENT = "NodeManagerDeployment"
    const val TYPE_SERVER = "Server"
    const val TYPE_ENVIRONMENT = "Environment"
    const val TYPE_BUSINESSGROUP = "BusinessGroup"
}

data class NodeManagerDeployment (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?,
        var applications: MutableList<Application>,
        var artifacts: MutableList<Artifact>,
        var databases: MutableList<Database>,
        var applicationInstances: MutableList<ApplicationInstance>
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String) : this(null, null, name, null, null, ArrayList(), ArrayList(), ArrayList(), ArrayList())
    override  val objectType = CmdbType.NodeManagemerDeployment

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        applications.forEach { json.append("Applications", it.id) }
        artifacts.forEach{ json.append("Artifacts", it.id) }
        databases.forEach{ json.append("Databases", it.id) }
        if(applicationInstances.isNotEmpty()) json.append("ApplicationInstances",  applicationInstances.map {it.id}.joinToString(","))
        return json
    }
}

data class Application(
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime? = null,
        override var updated: LocalDateTime? = null
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String) : this(null, null, name, null, null)
    constructor(origin: Application, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated)
    override  val objectType = CmdbType.Application
}

data class Artifact (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?,
        val partOf: Application?,
        val groupId: String,
        val version: String,
        val artifactId: String
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String, groupId: String, version: String, artifactId: String):
            this(null, null, name, null, null, null, groupId, version, artifactId)
    constructor(name: String, groupId: String, version: String, artifactId: String, partOf: Application):
            this(null, null, name, null, null, partOf, groupId, version, artifactId)
    constructor(origin: Artifact, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated, origin.partOf, origin.groupId, origin.version, origin.artifactId)
    override  val objectType = CmdbType.Artifact

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        json.append("GroupId", groupId)
        json.append("Version", version)
        json.append("ArtifactID", artifactId)
        if(partOf != null) {
            json.append("PartOf", partOf.id)
        }
        return json
    }
}

data class Database (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String) : this(null, null, name, null, null)
    constructor(origin: Database, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated)
    override  val objectType = CmdbType.Database
}

data class Server (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String) : this(null, null, name, null, null)
    constructor(origin: Server, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated)
    override  val objectType = CmdbType.Server
}

data class Environment (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?,
        val businessGroup: BusinessGroup?
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String, businessGroup: BusinessGroup) : this(null, null, name, null, null, businessGroup)
    constructor(name: String) : this(null, null, name, null, null, null)
    constructor(origin: Environment, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated, origin.businessGroup)
    override  val objectType = CmdbType.Environment
    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        if(businessGroup != null) {
            json.append("BusinessGroup", businessGroup.id)
        }
        return json
    }
}


data class BusinessGroup (
        override var id : Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String) : this(null, null, name, null, null)
    constructor(origin: BusinessGroup, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated)
    override  val objectType = CmdbType.BusinessGroup
}

data class ApplicationInstance (
        override var id: Int? = null,
        override var key: String? = null,
        override val name: String,
        override var created: LocalDateTime?,
        override var updated: LocalDateTime?,
        var runningOn: Server?,
        var environment: Environment?
) : BaseCMDBObject(id, key, name, created, updated) {
    constructor(name: String, runningOn: Server?, environment: Environment?):
            this(null, null, name, null, null, runningOn, environment)
    constructor(origin: ApplicationInstance, id: Int) :
            this(id, origin.key, origin.name, origin.created, origin.updated, origin.runningOn, origin.environment)
    override  val objectType = CmdbType.ApplicationInstance

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        if(runningOn != null) json.append("RunningOn", runningOn?.id)
        if(environment != null) json.append("Environment", environment?.id)
        return json
    }
}


abstract class BaseCMDBObject (
    open var id: Int? = null,
    open var key: String? = null,
    open val name: String,
    open var created: LocalDateTime? = null,
    open var updated: LocalDateTime? = null
){
    abstract val objectType: CmdbType

    fun toJson() : JSONObject {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val strJson = objectMapper.writeValueAsString(this)
        return JSONObject(strJson)
    }

    open fun toMinimalJson() : JSONObject{
        val jsonObject = JSONObject()
        jsonObject.append("Name", name)
        return jsonObject
    }
}

enum class CmdbType(val id: Int) {
    Application(415),
    ApplicationDeployment(411),
    ApplicationInstance(410),
    Artifact(416),
    Cluster(405),
    Database(406),
    BusinessGroup(461),
    Environment(404),
    NodeManagemerDeployment(412),
    Server(402),

    OpenShiftDeployment(413),
    ManualDeployment(414)
}