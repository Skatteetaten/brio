package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.brio.domain.Application
import no.skatteetaten.aurora.brio.domain.Artifact
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
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
    fun `construct simple object`() {
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
    fun `construct complete NodeManagerDeployment object`() {
        val jsonString = """
            {
                "ApplicationInstances": "",
                "Applications": {
                  "created": "23/01/20 16:28",
                  "hasAvatar": false,
                  "id": 69521,
                  "label": "Brio",
                  "name": "Brio",
                  "objectKey": "NOD1-69521",
                  "timestamp": 1579793286432,
                  "updated": "23/01/20 16:28"
                },
                "Artifacts": [
                  {
                    "created": "20/01/20 14:16",
                    "hasAvatar": false,
                    "id": 69462,
                    "label": "artifact2",
                    "name": "artifact2",
                    "objectKey": "NOD1-69462",
                    "timestamp": 1579698564438,
                    "updated": "22/01/20 14:09"
                  },
                  {
                    "created": "23/01/20 15:16",
                    "hasAvatar": false,
                    "id": 69516,
                    "label": "JFW_Test artifact",
                    "name": "JFW_Test artifact",
                    "objectKey": "NOD1-69516",
                    "timestamp": 1579788984273,
                    "updated": "23/01/20 15:16"
                  }
                ],
                "Created": "23/01/20 18:00",
                "Databases": {
                  "created": "27/01/20 12:51",
                  "hasAvatar": false,
                  "id": 69921,
                  "label": "testDB",
                  "name": "testDB",
                  "objectKey": "NOD1-69921",
                  "timestamp": 1580125918103,
                  "updated": "27/01/20 12:51"
                },
                "Key": "NOD1-69527",
                "Name": "TestDeployment",
                "Updated": "27/01/20 12:58",
                "id": 69527,
                "objectType": "NodeManagerDeployment"
              }
        """
        val obj = builder.construct(jsonString)
        assertNotNull(obj)
        assertThat(obj).isInstanceOf(NodeManagerDeployment::class)
        assertThat((obj as NodeManagerDeployment).applications.size).isEqualTo(1)
        assertThat(obj.applications[0]).isInstanceOf(Application::class.java)
        assertThat(obj.artifacts.size).isEqualTo(2)

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