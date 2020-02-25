package no.skatteetaten.aurora.brio.service

import no.skatteetaten.aurora.brio.domain.*
import okhttp3.mockwebserver.MockResponse
import kotlin.random.Random

object CMDBMockUtil {

    val artifactResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                    [
                      {
                        "Application": "",
                        "ArtifactID": "TestArtifact",
                        "Created": "20/02/20 15:59",
                        "GroupId": "no.jfw.test",
                        "Key": "NOD-174067",
                        "Name": "TestArtifact",
                        "Updated": "20/02/20 15:59",
                        "Version": "0.0.1",
                        "id": 174067,
                        "objectType": "Artifact"
                      }
                    ]
                """)

    val nodeManagerResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                  {
                    "Application": {
                      "created": "20/02/20 15:59",
                      "hasAvatar": false,
                      "id": 174064,
                      "label": "TestApp",
                      "name": "TestApp",
                      "objectKey": "NOD-174064",
                      "timestamp": 1582210764468,
                      "updated": "20/02/20 15:59",
                      "objectType": "Application"
                    },
                    "ApplicationInstance": {
                      "created": "20/02/20 15:59",
                      "hasAvatar": false,
                      "id": 174070,
                      "label": "TestApplicationInstance",
                      "name": "TestApplicationInstance",
                      "objectKey": "NOD-174070",
                      "timestamp": 1582210765599,
                      "updated": "20/02/20 15:59",
                      "objectType": "ApplicationInstance"
                    },
                    "Artifact": {
                      "created": "20/02/20 15:59",
                      "hasAvatar": false,
                      "id": 174067,
                      "label": "TestArtifact",
                      "name": "TestArtifact",
                      "objectKey": "NOD-174067",
                      "timestamp": 1582210765025,
                      "updated": "20/02/20 15:59",
                      "objectType": "Artifact"
                    },
                    "Created": "20/02/20 15:59",
                    "Database": [
                      {
                        "created": "20/02/20 15:59",
                        "hasAvatar": false,
                        "id": 174065,
                        "label": "MyDBTest",
                        "name": "MyDBTest",
                        "objectKey": "NOD-174065",
                        "timestamp": 1582210764661,
                        "updated": "20/02/20 15:59",
                        "objectType": "Database"
                      },
                      {
                        "created": "20/02/20 15:59",
                        "hasAvatar": false,
                        "id": 174066,
                        "label": "MyDBTest2",
                        "name": "MyDBTest2",
                        "objectKey": "NOD-174066",
                        "timestamp": 1582210764841,
                        "updated": "20/02/20 15:59",
                        "objectType": "Database"
                      }
                    ],
                    "Key": "NOD-174071",
                    "Name": "TestDeployment",
                    "Updated": "20/02/20 15:59",
                    "id": 174071,
                    "objectType": "NodeManagerDeployment"
                  }
                ]
        """)

    val empty = MockResponse()
            .setResponseCode(200)
            .setBody("")

    fun createObject(id: Int, name: String): MockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
            {
              "created": "24/02/20 13:45",
              "hasAvatar": false,
              "id": $id,
              "label": "$name",
              "name": "$name",
              "objectKey": "NOD-$id",
              "timestamp": 1582548319992,
              "updated": "24/02/20 13:45"
            }
        """)

    /*fun findObject(id: Int, name: String, type: String): MockResponse {
        return MockResponse().setResponseCode(200).setBody("[ ${getBaseCmdbObject(id, name, type)} ]")
    }*/

    fun nodeManager(
        id: Int,
        name: String,
        application: Array<Application>?,
        applicationInstance: Array<ApplicationInstance>?,
        artifact: Array<Artifact>?,
        database: Array<Database>?
    ): MockResponse {
        val applicationString = application?.joinToString(separator = ",", prefix = "[", postfix = "]") { getBaseCmdbObject(it) }
                ?: "\"\""
        val applicationInstanceString = applicationInstance?.joinToString(separator = ",", prefix = "[", postfix = "]") { getApplicationInstance(it) }
                ?: "\"\""
        val artifactString = artifact?.joinToString(separator = ",", prefix = "[", postfix = "]") { getBaseCmdbObject(it) }
                ?: "\"\""
        val databaseString = database?.joinToString(separator = ",", prefix = "[", postfix = "]") { getBaseCmdbObject(it) }
                ?: "\"\""
        val body = """
            [
              {
                "Application": $applicationString,
                "ApplicationInstance": $applicationInstanceString,
                "Artifact": $artifactString,
                "Created": "24/02/20 13:45",
                "Database": $databaseString,
                "Key": "NOD-$id",
                "Name": "$name",
                "Updated": "24/02/20 13:45",
                "id": $id,
                "objectType": "NodeManagerDeployment"
              }
            ]
        """
        return MockResponse()
                .setResponseCode(200)
                .setBody(body)
    }

    private fun getBaseCmdbObject(instance: BaseCMDBObject?): String {
        if (instance == null) return "\"\""
        return """
            {
                ${getBaseCmdObjectData(instance)}
            }
        """
    }

    private fun getApplicationInstance(instance: ApplicationInstance): String {
        return """
            {
                ${getBaseCmdObjectData(instance)},
                "Server": ${getBaseCmdbObject(instance.runningOn)},
                "Environment": ${getBaseCmdbObject(instance.environment)}
            }
        """
    }

    private fun getBaseCmdObjectData(instance: BaseCMDBObject): String {
        var id = instance.id ?: Random.nextInt(0, 100)
        return """
            "Created": "24/02/20 13:45",
            "Key": "NOD-$id",
            "Name": "${instance.name}",
            "Updated": "24/02/20 13:45",
            "id": $id,
            "objectType": "${instance.objectType.name}"
        """
    }
}