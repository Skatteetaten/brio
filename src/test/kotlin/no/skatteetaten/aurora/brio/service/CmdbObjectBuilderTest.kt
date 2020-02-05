package no.skatteetaten.aurora.brio.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import io.mockk.every
import io.mockk.mockk
import no.skatteetaten.aurora.brio.domain.Application
import no.skatteetaten.aurora.brio.domain.Artifact
import no.skatteetaten.aurora.brio.domain.BaseCMDBObject
import no.skatteetaten.aurora.brio.domain.NodeManagerDeployment
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CmdbObjectBuilderTest {

    private val cmdbClient = mockk<CMDBClient>()
    private var builder = CmdbObjectBuilder()

    @BeforeEach
    fun init() {
        builder.cmdbClient = cmdbClient
        every { cmdbClient.findByKey("NOD1-AP1") } returns JSONObject("""
            {
                "Created": "31/01/20 15:09",
                "Key": "NOD1-AP1",
                "Name": "TestApp",
                "Updated": "31/01/20 15:09",
                "id": 1,
                "objectType": "Application"
            }
        """)

        every { cmdbClient.findByKey("NOD1-A1") } returns JSONObject("""
          {
            "ArtifactID": "skattefinn-leveransepakke",
            "Created": "20/01/20 14:16",
            "GroupId": "ske.arbeidsflate.skattefinn",
            "Key": "NOD1-A1",
            "Name": "artifact1",
            "PartOf": "",
            "Updated": "22/01/20 14:09",
            "Version": "0.0.1",
            "id": 1,
            "objectType": "Artifact"
          }
        """)

        every { cmdbClient.findByKey("NOD1-A2") } returns JSONObject("""
          {
            "ArtifactID": "skattefinn-leveransepakke",
            "Created": "20/01/20 14:16",
            "GroupId": "ske.arbeidsflate.skattefinn",
            "Key": "NOD1-A2",
            "Name": "artifact2",
            "PartOf": "",
            "Updated": "22/01/20 14:09",
            "Version": "0.0.1",
            "id": 2,
            "objectType": "Artifact"
          }
        """)

        every { cmdbClient.findByKey("NOD1-D1") } returns JSONObject("""
          {
            "Created": "31/01/20 15:09",
            "Key": "NOD1-D1",
            "Name": "MyDBTest",
            "Updated": "31/01/20 15:09",
            "id": 1,
            "objectType": "Database"
          }
        """)
    }

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
        val obj = builder.buildCmdObject(jsonString)
        assertNotNull(obj)
        assertThat(obj).isInstanceOf(Artifact::class)
    }

    @Test
    fun `construct complete NodeManagerDeployment object`() {
        val jsonString = """
            {
                "ApplicationInstances": "",
                "Applications": { "objectKey": "NOD1-AP1" },
                "Artifacts": [
                  { "objectKey": "NOD1-A1"}, 
                  { "objectKey": "NOD1-A2"}
                ],
                "Created": "23/01/20 18:00",
                "Databases": { "objectKey": "NOD1-D1" },
                "Key": "NOD1-69527",
                "Name": "TestDeployment",
                "Updated": "27/01/20 12:58",
                "id": 69527,
                "objectType": "NodeManagerDeployment"
              }
        """
        val obj = builder.buildCmdObject(jsonString)
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
            "PartOf": { "objectKey": "NOD1-AP1" }, 
            "Updated": "22/01/20 14:09", 
            "Version": "0.0.1", 
            "id": 69461, 
            "objectType": "Artifact"
          }"""
        val obj = builder.buildCmdObject(jsonString)
        assertNotNull(obj)
        assertThat(obj).isInstanceOf(Artifact::class)
        if (obj is Artifact) {
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
                "objectKey": "NOD1-AP1",
                "timestamp": 1579698533381,
                "updated": "22/01/20 14:08"
            }"""
        val node = JSONObject(childJson)
        val obj = builder.constructChildNode(node)
        assertNotNull(obj)
        if (obj != null) {
            assertThat(obj).isInstanceOf(BaseCMDBObject::class)
        }
    }
}