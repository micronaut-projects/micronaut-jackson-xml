plugins {
    id("io.micronaut.build.internal.bom")
}
micronautBuild {
    binaryCompatibility {
        enabled.set(true)
        baselineVersion.set("4.0.0-M8")
    }
}
