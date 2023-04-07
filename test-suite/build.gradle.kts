plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("java-library")
}

dependencies {
    testAnnotationProcessor(platform(mn.micronaut.core.bom))
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(platform(mn.micronaut.core.bom))
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testRuntimeOnly(libs.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
