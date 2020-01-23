# Micronaut Jackson XML

[![Maven Central](https://img.shields.io/maven-central/v/io.micronaut.xml/micronaut-jackson-xml.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.micronaut.xml%22%20AND%20a:%22micronaut-jackson-xml%22)
[![Build Status](https://github.com/micronaut-projects/micronaut-jackson-xml/workflows/Java%20CI/badge.svg)](https://github.com/micronaut-projects/micronaut-jackson-xml/actions)

When this library is added to a Micronaut application, it creates the beans necessary to allow for the serialization and deserialization of XML. Both the client and server are supported in a similar manner to the standard support of JSON. Jackson is used to do the conversion of the XML to objects.

Because XML has no array boundaries, the entire request body must be buffered into memory before deserialization can occur. The standard request body size limits apply here as well.


## Documentation

Simply add the dependency to your Micronaut 1.3 or above project. For example in Gradle:

```groovy
implementation "io.micronaut.xml:micronaut-jackson-xml"
````

or Maven:

```xml
<dependency>
  <groupId>io.micronaut.xml</groupId>
  <artifactId>micronaut-jackson-xml</artifactId>
  <scope>runtime</scope>
</dependency>
```

See the [Jackson XML](https://github.com/FasterXML/jackson-dataformat-xml) documentation for more information.

## Snapshots and Releases

Snaphots are automatically published to [JFrog OSS](https://oss.jfrog.org/artifactory/oss-snapshot-local/) using [Github Actions](https://github.com/micronaut-projects/micronaut-aws/actions).

See the documentation in the [Micronaut Docs](https://docs.micronaut.io/latest/guide/index.html#usingsnapshots) for how to configure your build to use snapshots.

Releases are published to JCenter and Maven Central via [Github Actions](https://github.com/micronaut-projects/micronaut-jackson-xml/actions).

A release is performed with the following steps:

* [Edit the version](https://github.com/micronaut-projects/micronaut-jackson-xml/edit/master/gradle.properties) specified by `projectVersion` in `gradle.properties` to a semantic, unreleased version. Example `1.0.0`
* [Create a new release](https://github.com/micronaut-projects/micronaut-jackson-xml/releases/new). The Git Tag should start with `v`. For example `v1.0.0`.
* [Monitor the Workflow](https://github.com/micronaut-projects/micronaut-jackson-xml/actions?query=workflow%3ARelease) to check it passed successfully.
* Celebrate!
