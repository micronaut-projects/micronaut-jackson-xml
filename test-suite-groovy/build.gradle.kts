plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("java-library")
    id("groovy")
}

dependencies {
    testImplementation(platform(mn.micronaut.core.bom))
    testCompileOnly(mn.micronaut.inject.groovy)
    testImplementation(mnTest.micronaut.test.spock)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testRuntimeOnly(mnLogging.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
