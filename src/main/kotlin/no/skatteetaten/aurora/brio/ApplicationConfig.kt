package no.skatteetaten.aurora.brio

import mu.KotlinLogging
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ApplicationConfig {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()
}