plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    kaptTest(mn.micronaut.inject.java)
    testImplementation(libs.kotlin.stdlib.jdk8)

    testImplementation(platform(mn.micronaut.core.bom))
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testRuntimeOnly(mnLogging.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
