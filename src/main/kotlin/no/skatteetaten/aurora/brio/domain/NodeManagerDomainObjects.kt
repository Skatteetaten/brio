package no.skatteetaten.aurora.brio.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.json.JSONObject
import java.time.LocalDateTime

object CmdbStatic {
    val mapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().build(),
                    ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE)

    const val CMDB_DATE_FORMAT = "yy/MM/dd HH:mm"
    const val OBJECT_TYPE = "objectType"
    const val ID = "id"
    const val KEY = "Key"
    const val NAME = "Name"
    const val CREATED = "Created"
    const val UPDATED = "Updated"
    const val PART_OF = "Application" // "PartOf"
    const val APPLICATIONS = "Application" // "Applications"
    const val VERSION = "Version"
    const val ARTIFACT_ID = "ArtifactID"
    const val ATTRIBUTE = "attribute"
    const val GROUP_ID = "GroupId"
    const val ARTIFACTS = "Artifact" // "Artifacts"
    const val DATABASES = "Database" // "Databases"
    const val OBJECT_KEY = "objectKey"
    const val APPLICATION_INSTANCES = "ApplicationInstance" // "ApplicationInstances"
    const val RUNNING_ON = "Server" // "RunningOn"
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

data class NodeManagerDeployment(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?,
    @JsonProperty(CmdbStatic.APPLICATIONS)
    var applications: Array<Application>?,
    @JsonProperty(CmdbStatic.ARTIFACTS)
    var artifacts: Array<Artifact>?,
    @JsonProperty(CmdbStatic.DATABASES)
    var databases: Array<Database>?,
    @JsonProperty(CmdbStatic.APPLICATION_INSTANCES)
    var applicationInstances: Array<ApplicationInstance>?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.NodeManagemerDeployment) {
    constructor(name: String) : this(null, null, name, null, null, null, null, null, null)

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        applications?.forEach { json.append(CmdbStatic.APPLICATIONS, it.id) }
        artifacts?.forEach { json.append(CmdbStatic.ARTIFACTS, it.id) }
        databases?.forEach { json.append(CmdbStatic.DATABASES, it.id) }
        applicationInstances?.forEach { json.append(CmdbStatic.APPLICATION_INSTANCES, it.id) }
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodeManagerDeployment

        if (id != other.id) return false
        if (key != other.key) return false
        if (name != other.name) return false
        if (created != other.created) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (key?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + (created?.hashCode() ?: 0)
        result = 31 * result + (updated?.hashCode() ?: 0)
        return result
    }
}

data class Application(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime? = null,
    override var updated: LocalDateTime? = null
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.Application) {
    constructor(name: String) : this(null, null, name, null, null)
}

class Artifact(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?,
    @JsonProperty(CmdbStatic.PART_OF)
    val partOf: Application?,
    @JsonProperty(CmdbStatic.GROUP_ID)
    val groupId: String?,
    @JsonProperty(CmdbStatic.VERSION)
    val version: String?,
    @JsonProperty(CmdbStatic.ARTIFACT_ID)
    val artifactId: String?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.Artifact) {
    constructor(name: String, groupId: String, version: String, artifactId: String) :
            this(null, null, name, null, null, null, groupId, version, artifactId)

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        json.append(CmdbStatic.GROUP_ID, groupId)
        json.append(CmdbStatic.VERSION, version)
        json.append(CmdbStatic.ARTIFACT_ID, artifactId)
        if (partOf != null) {
            json.append(CmdbStatic.PART_OF, partOf.id)
        }
        return json
    }
}

data class Database(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.Database) {
    constructor(name: String) : this(null, null, name, null, null)
}

data class Server(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.Server) {
    constructor(name: String) : this(null, null, name, null, null)
}

data class Environment(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?,
    val businessGroup: BusinessGroup?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.Environment) {
    constructor(name: String, businessGroup: BusinessGroup) : this(null, null, name, null, null, businessGroup)

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        if (businessGroup != null) {
            json.append(CmdbStatic.BUSINESSGROUP, businessGroup.id)
        }
        return json
    }
}

data class BusinessGroup(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime? = null,
    override var updated: LocalDateTime? = null
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.BusinessGroup) {
    constructor(name: String) : this(null, null, name, null, null)
}

data class ApplicationInstance(
    override var id: Int? = null,
    override var key: String? = null,
    override var name: String,
    override var created: LocalDateTime?,
    override var updated: LocalDateTime?,
    @JsonProperty(CmdbStatic.RUNNING_ON)
    var runningOn: Server?,
    @JsonProperty(CmdbStatic.ENVIRONMENT)
    var environment: Environment?
) : BaseCMDBObject(id, key, name, created, updated, CmdbType.ApplicationInstance) {
    constructor(name: String, runningOn: Server?, environment: Environment?) :
            this(null, null, name, null, null, runningOn, environment)

    override fun toMinimalJson(): JSONObject {
        val json = super.toMinimalJson()
        if (runningOn != null) json.append(CmdbStatic.RUNNING_ON, runningOn?.id)
        if (environment != null) json.append(CmdbStatic.ENVIRONMENT, environment?.id)
        return json
    }
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "objectType"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = Artifact::class, name = CmdbStatic.TYPE_ARTIFACT),
        JsonSubTypes.Type(value = BusinessGroup::class, name = CmdbStatic.TYPE_BUSINESSGROUP),
        JsonSubTypes.Type(value = Environment::class, name = CmdbStatic.TYPE_ENVIRONMENT),
        JsonSubTypes.Type(value = Application::class, name = CmdbStatic.TYPE_APPLICATION),
        JsonSubTypes.Type(value = ApplicationInstance::class, name = CmdbStatic.TYPE_APPLICATION_INSTANCE),
        JsonSubTypes.Type(value = Database::class, name = CmdbStatic.TYPE_DATABASE),
        JsonSubTypes.Type(value = NodeManagerDeployment::class, name = CmdbStatic.TYPE_NODE_MANAGER_DEPLOYMENT),
        JsonSubTypes.Type(value = Server::class, name = CmdbStatic.TYPE_SERVER)
)
@JsonIgnoreProperties(ignoreUnknown = false, value = ["label", "hasAvatar", "timestamp"])
abstract class BaseCMDBObject(
    id: Int? = null,
    key: String? = null,
    name: String,
    created: LocalDateTime? = null,
    updated: LocalDateTime? = null,
    @JsonIgnore
    open var objectType: CmdbType
) {

    open var id: Int? = id
        @JsonGetter("id")
        get() { return id }

    open var key: String? = key
        @JsonGetter(CmdbStatic.KEY)
        @JsonAlias(CmdbStatic.OBJECT_KEY)
        get() { return key }

    open var name: String = name
        @JsonGetter(CmdbStatic.NAME)
        @JsonAlias("name")
        get() { return name }

    open var created: LocalDateTime? = created
        @JsonGetter(CmdbStatic.CREATED)
        @JsonAlias("created")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CmdbStatic.CMDB_DATE_FORMAT)
        get() { return created }

    open var updated: LocalDateTime? = updated
        @JsonGetter(CmdbStatic.UPDATED)
        @JsonAlias("updated")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CmdbStatic.CMDB_DATE_FORMAT)
        get() { return updated }

    fun toJson(): JSONObject {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val strJson = objectMapper.writeValueAsString(this)
        return JSONObject(strJson)
    }

    open fun toMinimalJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.append(CmdbStatic.NAME, name)
        return jsonObject
    }
}

enum class CmdbType(val id: Int, name: String) {
    Application(1093, CmdbStatic.TYPE_APPLICATION),
    ApplicationDeployment(1088, "ApplicationDeployment"),
    ApplicationInstance(1092, CmdbStatic.TYPE_APPLICATION_INSTANCE),
    Artifact(1094, CmdbStatic.TYPE_ARTIFACT),
    Cluster(1095, "Cluster"),
    Database(1097, CmdbStatic.TYPE_DATABASE),
    BusinessGroup(1096, CmdbStatic.TYPE_BUSINESSGROUP),
    Environment(1098, CmdbStatic.TYPE_ENVIRONMENT),
    NodeManagemerDeployment(1100, CmdbStatic.TYPE_NODE_MANAGER_DEPLOYMENT),
    Server(1099, CmdbStatic.TYPE_SERVER),

    OpenShiftDeployment(1102, "OpenShiftDeployment"),
    ManualDeployment(1101, "ManualDeployment"),
    EksisterendeTyperAsset(1081, "ExisterendeTyperAsset")
}