package no.skatteetaten.aurora.brio.security

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

private val logger = KotlinLogging.logger {}

/**
 * Component for reading the shared secret used for authentication. You may specify the shared secret directly using
 * the cmdb.token.value property, or specify a file containing the secret with the cmdb.token.location property.
 */
@Component
class CmdbSecretReader(
    @Value("\${cmdb.token.location:}") private val secretLocation: String?,
    @Value("\${cmdb.token.value:}") private val secretValue: String?
) {

    val secret = initSecret(secretValue)

    private fun initSecret(secretValue: String?) =
        if (secretLocation.isNullOrEmpty() && secretValue.isNullOrEmpty()) {
            throw IllegalArgumentException("Either cmdb.token.location or cmdb.token.value must be specified")
        } else {
            if (secretValue.isNullOrEmpty()) {
                val secretFile = File(secretLocation).absoluteFile
                try {
                    logger.info("Reading token from file {}", secretFile.absolutePath)
                    secretFile.readText()
                } catch (e: IOException) {
                    throw IllegalStateException("Unable to read shared secret from specified location [${secretFile.absolutePath}]")
                }
            } else {
                secretValue
            }
        }
}