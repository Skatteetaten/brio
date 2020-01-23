package no.skatteetaten.aurora.brio.service

import no.skatteetaten.aurora.brio.domain.*
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

    fun construct(node: JSONObject): BaseCMDBObject {
        val objectType = node.getString("objectType")
        val id : Int? = if(node.has("id")) node.getInt("id") else null
        val key : String? = if(node.has("Key")) node.getString("Key") else null
        val name = node.getString("Name")
        val created = LocalDateTime.parse(node.getString("Created"), dateFormatter)
        val updated = LocalDateTime.parse(node.getString("Updated"), dateFormatter)

        var cmdbObject: BaseCMDBObject = Application(null,null, "test", LocalDateTime.now(), LocalDateTime.now())
        when (objectType) {
            "Artifact" -> cmdbObject = Artifact(
                    id, key, name, created, updated,
                    if(node.has("PartOf")) constructChildNode(node.getJSONObject("PartOf")) as Application? else null,
                    node.getString("GroupId"),
                    node.getString("Version"),
                    node.getString("ArtifactID")
            )
            "ApplicationInstance" -> cmdbObject = ApplicationInstance(id, key, name, created, updated)
            "Database" -> cmdbObject = Database(id, key, name, created, updated,
                    node.getString("attribute")
            )
            "Application" -> cmdbObject = Application(id, key, name, created, updated)
            "NodeManagerDeployment" -> {
                val applications = if(node.has("Applications") && node.get("Applications") != "")
                    node.getJSONArray("Applications").map { constructChildNode(it as JSONObject) as Application }.toMutableList() else ArrayList()
                val artifacts = if(node.has("Artifacts") && node.get("Artifacts") != "")
                    node.getJSONArray("Artifacts").map{ constructChildNode(it as JSONObject) as Artifact }.toMutableList() else ArrayList()
                val databases = if(node.has("Databases")  && node.get("Databases") != "")
                    node.getJSONArray("Databases").map{ constructChildNode(it as JSONObject) as Database }.toMutableList() else ArrayList()
                val applicationInstances = if(node.has("ApplicationInstances")  && node.get("ApplicationInstances") != "")
                    node.getJSONArray("ApplicationInstances").map{ constructChildNode(it as JSONObject) as ApplicationInstance }.toMutableList() else ArrayList()
                cmdbObject = NodeManagerDeployment(id, key, name, created, updated, applications, artifacts, databases, applicationInstances)
            }
        }
        return cmdbObject
    }

    fun constructChildNode(node: JSONObject) : BaseCMDBObject?{
        val key = (if(node.has("objectKey")) node.getString("objectKey") else null) ?: return null
        val child = cmdbClient.findByKey(key) ?: return null
        if(child.isEmpty) return null
        return construct(child)
    }
}