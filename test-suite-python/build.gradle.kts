plugins {
    id("io.micronaut.build.internal.jackson-xml-examples")
    id("java-library")
    id("io.micronaut.build.internal.java-base")
    id("io.micronaut.build.internal.python")
}

dependencies {
    testImplementation(platform(mn.micronaut.core.bom))
    // The Python compiler (micronaut-inject-python) takes the (jar-resolved) compile classpath as its
    // annotation processor path, so the processors are testImplementation (not testAnnotationProcessor).
    testImplementation(mn.micronaut.inject.python.test)
    testImplementation(mn.micronaut.context.python)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautJacksonXml)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(mnTest.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("micronaut.python.pool.enabled", "false")
    // a Truffle host-interop assertion trips on varargs overloads
    enableAssertions = false
}
