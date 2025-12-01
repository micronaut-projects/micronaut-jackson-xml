package io.micronaut.xml.jackson.docs

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import org.jspecify.annotations.NonNull

@Introspected
@JacksonXmlRootElement(localName = "book")
class Book {
    @NonNull
    String name
}
