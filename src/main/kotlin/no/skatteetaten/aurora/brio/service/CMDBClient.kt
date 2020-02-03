package no.skatteetaten.aurora.brio.service

import mu.KotlinLogging
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import no.skatteetaten.aurora.brio.domain.CmdbType
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.lang.IllegalArgumentException

private val logger = KotlinLogging.logger {}

@Service
class CMDBClient {
    companion object {
        const val ACCESS_TOKEN = "DqhbstdJjhgo6ke6Rj1KbjdhA2qbUv0s"
        private const val BASE_URL = "https://ref-cmdb.sits.no"
        private const val SCHEMA_ID = 181
        const val SCHEMA_URL = "${BASE_URL}/auto/schema/${SCHEMA_ID}"
    }

    fun findByKey(key: String): JSONObject? {
        val iqlUrl = "${SCHEMA_URL}/instance/iql/Key=${key}"
        val response =  doGet(iqlUrl)
        return if(! response.isEmpty) response.getJSONObject(0) else null
    }

    fun findById(id: Int): JSONObject? {
        val iqlUrl = "${SCHEMA_URL}/instance/iql/objectId=${id}"
        val response =  doGet(iqlUrl)
        return if(! response.isEmpty) response.getJSONObject(0) else null
    }

    fun findObjectOfTypeByName(type: CmdbType, name: String): JSONArray {
        val iqlUrl = "${SCHEMA_URL}/instance/iql/objectTypeId=${type.id} AND Name=${name}"
        return doGet(iqlUrl);
    }

    fun findObjectOfTypeByArtifactIdAndGroupId(type: CmdbType, artifactId: String, groupId: String): JSONArray{
        val iqlUrl = "${SCHEMA_URL}/instance/iql/objectTypeId=${type.id} AND ArtifactID=${artifactId} AND GroupId=${groupId}"
        return doGet(iqlUrl);
    }

    fun findObjectOfTypeByByArtifactIdAndGroupIdVersion(type: CmdbType, artifactId: String, groupId: String, version: String): JSONArray {
        val iqlUrl = "${SCHEMA_URL}/instance/iql/objectTypeId=${type.id} AND ArtifactID=${artifactId} AND GroupId=${groupId} AND Version=${version}"
        return doGet(iqlUrl);
    }

    fun createObject(instance: BaseCMDBObject): JSONObject {
        if(instance.key != null){
            throw IllegalArgumentException("Can not create new objects in CMDB if it already exists. Existing key ${instance.key}")
        }
        val data = instance.toMinimalJson()
        return createObject(instance.objectType, data)
    }

    fun createObject(type: CmdbType, instance: JSONObject): JSONObject {
        if(instance.has("Key") && instance.has("Key") != null){
            throw IllegalArgumentException("Can not create new objects in CMDB if it already exists. Existing key ${instance.getString("key") }")
        }
        val postUrl = "${SCHEMA_URL}/type/${type.id}/instance"
        return doPost(postUrl, instance)

    }

    fun deleteObject(type: CmdbType, instance: JSONObject) : Boolean {
        var id : Int?
        if(instance.has("id")){
            id = instance.getInt("id")
        }else if(instance.has("objectKey")){
            val key = instance.getString("objectKey")
            id = findByKey(key)?.getInt("id")
        }else{
            return false
        }

        val delUrl = "${SCHEMA_URL}/type/${type.id}/instance/${id}"
        doDelete(delUrl)
        return true;
    }

    fun deleteObject(instance: BaseCMDBObject) : Boolean {
        instance.key ?: throw IllegalArgumentException("Can not delete objects in CMDB without a key.")
        return deleteObject(instance.objectType, instance.toJson())
    }

    private fun doDelete(url: String): String? {
        val headers = getHeaders()
        val entity = HttpEntity<String>(headers)
        return RestTemplate().exchange(url, HttpMethod.DELETE, entity, String::class.java).body
    }

    private fun doPost(url: String, data: JSONObject) : JSONObject {
        val headers = getHeaders()
        val entity = HttpEntity(data.toString(), headers)



        val response = try {
            RestTemplate().exchange(url, HttpMethod.POST, entity, String::class.java)
        }catch (e: HttpClientErrorException) {
            logger.warn("failed response CMDB for request: $url containing data:\n$data\n"+
                "Response was: ${e.message}")
            throw e
        }

        val body = response.body
                ?: throw Exception("Could not perform post, body was empty. Response code from CMDB: ${response.statusCode}")
        return JSONObject(body)

    }

    private fun doGet(url: String): JSONArray {
        val headers = getHeaders()
        val entity = HttpEntity<String>(headers)
        val response = RestTemplate().exchange(url, HttpMethod.GET, entity, String::class.java).body
        return JSONArray(response)
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer ${ACCESS_TOKEN}")
        return headers
    }
}
