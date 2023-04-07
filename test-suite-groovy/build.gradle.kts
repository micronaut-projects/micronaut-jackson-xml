plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("java-library")
    id("groovy")
}

dependencies {
    testImplementation(platform(mn.micronaut.bom))
    testCompileOnly(mn.micronaut.inject.groovy)
    testImplementation(mn.micronaut.test.spock)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.jacksonXml)
    testRuntimeOnly(libs.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
