package no.skatteetaten.aurora.brio.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.json.JSONObject
import java.time.LocalDateTime


data class NodeManagerDeployment (
        override val id : Int? = null,
        override val key: String? = null,
        override val name: String,
        override val created: LocalDateTime?,
        override var updated: LocalDateTime?,
        val applications: MutableList<Application>,
        val artifacts: MutableList<Artifact>,
        val databases: MutableList<Database>,
        val applicationInstances: MutableList<ApplicationInstance>
) : BaseCMDBObject(id, key, name, created, updated) {
    override  val objectType = CmdbType.NodeManagemerDeployment

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        //json.append("Applications", attribute)
        //json.append("Artifacts", attribute)
        //json.append("Databases", attribute)
        //json.append("ApplicationInstances", attribute)
        return json
    }
}

data class Application (
        override val id : Int? = null,
        override val key: String? = null,
        override val name: String,
        override val created: LocalDateTime?,
        override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated) {
    override  val objectType = CmdbType.Application
}

data class Artifact (
        override val id : Int? = null,
        override val key: String? = null,
        override val name: String,
        override val created: LocalDateTime?,
        override var updated: LocalDateTime?,
        val partOf: Application?,
        val groupId: String,
        val version: String,
        val artifactId: String
) : BaseCMDBObject(id, key, name, created, updated) {
    override  val objectType = CmdbType.Artifact

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        json.append("GroupId", groupId)
        json.append("Version", version)
        json.append("ArtifactId", artifactId)
        if(partOf != null) {
            json.append("PartOf", partOf.id)
        }
        return json
    }
}

data class Database (
        override val id : Int? = null,
        override val key: String? = null,
        override val name: String,
        override val created: LocalDateTime?,
        override var updated: LocalDateTime?,
        val attribute: String
) : BaseCMDBObject(id, key, name, created, updated) {
    override  val objectType = CmdbType.Database

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        json.append("Attribute", attribute)
        return json
    }

}

data class ApplicationInstance (
        override val id: Int? = null,
        override val key: String? = null,
        override val name: String,
        override val created: LocalDateTime?,
        override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated) {
    override  val objectType = CmdbType.ApplicationInstance
}


abstract class BaseCMDBObject (
    open val id: Int? = null,
    open val key: String? = null,
    open val name: String,
    open val created: LocalDateTime?,
    open var updated: LocalDateTime?
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