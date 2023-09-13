plugins {
    id("io.micronaut.build.internal.jackson-xml-module")
}

dependencies {
    api(libs.jackson.dataformat.xml)
    api(mn.micronaut.jackson.databind)
    api(mn.micronaut.inject)
    api(mn.micronaut.http)
    testImplementation(libs.jaxb.api)
    compileOnly(mn.micronaut.http.server.netty)
    testImplementation(mn.reactor)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testRuntimeOnly(mn.snakeyaml)

}
