package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.brio.domain.Artifact
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import org.json.JSONObject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(CmdbObjectBuilder::class, CMDBClient::class)
internal class CmdbObjectBuilderTest {

    @Autowired
    lateinit var builder: CmdbObjectBuilder

    @Test
    fun constructSimpleObject() {
        val jsonString = """{
            "ArtifactID": "skattefinn-leveransepakke", 
            "Created": "20/01/20 14:16", 
            "GroupId": "ske.arbeidsflate.skattefinn", 
            "Key": "NOD1-69461", 
            "Name": "artifact1", 
            "Updated": "22/01/20 14:09", 
            "Version": "0.0.1", 
            "id": 69461, 
            "objectType": "Artifact"
          }"""
        val obj = builder.construct(jsonString)
        assertNotNull(obj)
        assertThat(obj).isInstanceOf(Artifact::class)
    }

    @Test
    fun testConstruct() {
        val jsonString = """{
            "ArtifactID": "skattefinn-leveransepakke", 
            "Created": "20/01/20 14:16", 
            "GroupId": "ske.arbeidsflate.skattefinn", 
            "Key": "NOD1-69461", 
            "Name": "artifact1", 
            "PartOf": {
              "created": "22/01/20 14:08", 
              "hasAvatar": false, 
              "id": 69501, 
              "label": "skattefinn-leveransepakke", 
              "name": "skattefinn-leveransepakke", 
              "objectKey": "NOD1-69501", 
              "timestamp": 1579698533381, 
              "updated": "22/01/20 14:08"
            }, 
            "Updated": "22/01/20 14:09", 
            "Version": "0.0.1", 
            "id": 69461, 
            "objectType": "Artifact"
          }"""
        val obj = builder.construct(jsonString)
        assertNotNull(obj)
        assertThat(obj).isInstanceOf(Artifact::class)
        if(obj is Artifact){
            assertThat(obj.partOf).isNotNull()
        }

    }

    @Test
    fun constructChildNode() {
        val childJson = """{
                "created": "22/01/20 14:08",
                "hasAvatar": false,
                "id": 69501,
                "label": "skattefinn-leveransepakke",
                "name": "skattefinn-leveransepakke",
                "objectKey": "NOD1-69501",
                "timestamp": 1579698533381,
                "updated": "22/01/20 14:08"
            }"""
        val node = JSONObject(childJson)
        val obj = builder.constructChildNode(node)
        assertNotNull(obj)
        if(obj != null) {
            assertThat(obj).isInstanceOf(BaseCMDBObject::class)
        }
    }
}