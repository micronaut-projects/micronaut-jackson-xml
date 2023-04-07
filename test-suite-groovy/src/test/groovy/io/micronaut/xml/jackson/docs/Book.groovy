package io.micronaut.xml.jackson.docs

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull

@Introspected
@JacksonXmlRootElement(localName = "book")
class Book {
    @NonNull
    String name
}
