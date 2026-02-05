package io.micronaut.xml.jackson.docs

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@Introspected
@JacksonXmlRootElement(localName = "book")
class Book {
    @NonNull
    String name
}
