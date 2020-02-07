plugins {
    id("org.springframework.cloud.contract")
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.50"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
    id("org.sonarqube") version "2.7.1"

    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("org.asciidoctor.convert") version "2.4.0"

    id("com.gorylenko.gradle-git-properties") version "2.1.0"
    id("com.github.ben-manes.versions") version "0.25.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.12"

    id("no.skatteetaten.gradle.aurora") version "3.2.0"
}

dependencies {
    implementation("uk.q3c.rest:hal-kotlin:0.5.4.0.db32476")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.3.50")
    implementation("io.projectreactor.addons:reactor-extra:3.2.3.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.codehaus.jackson:jackson-mapper-asl")
    implementation("org.json:json:20190722")
    implementation("org.springframework.security.oauth:spring-security-oauth2:2.2.1.RELEASE")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.19")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("no.skatteetaten.aurora:mockmvc-extensions-kotlin:1.0.0")
}