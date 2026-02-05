package io.micronaut.xml.jackson.docs

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull

@JacksonXmlRootElement(localName = "book")
@Introspected
class BookSaved {
    String name

    @NonNull
    @JacksonXmlProperty(isAttribute = true)
    String isbn
}
