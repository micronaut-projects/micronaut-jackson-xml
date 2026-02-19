plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("io.micronaut.build.internal.kotlin-kapt")
}

dependencies {
    kaptTest(mn.micronaut.inject.java)

    testImplementation(platform(mn.micronaut.core.bom))
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(mnTest.junit.platform.launcher)
    testRuntimeOnly("tools.jackson.module:jackson-module-kotlin")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
