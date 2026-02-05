plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("java-library")
    id("groovy")
}

dependencies {
    testImplementation(platform(mn.micronaut.core.bom))
    testCompileOnly(mn.micronaut.inject.groovy)
    testImplementation(mnTest.micronaut.test.spock)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testImplementation(mnTest.junit.platform.launcher)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
