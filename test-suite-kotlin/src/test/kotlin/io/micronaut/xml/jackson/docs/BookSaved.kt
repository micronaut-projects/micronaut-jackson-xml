package io.micronaut.xml.jackson.docs;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty
import io.micronaut.core.annotation.Introspected
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "book")
@Introspected
data class BookSaved(val name: String, @field:JacksonXmlProperty(isAttribute = true) val isbn: String)
