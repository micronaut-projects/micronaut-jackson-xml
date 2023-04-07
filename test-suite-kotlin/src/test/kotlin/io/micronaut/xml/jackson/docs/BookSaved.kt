package io.micronaut.xml.jackson.docs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected

@JacksonXmlRootElement(localName = "book")
@Introspected
data class BookSaved(val name: String, @field:JacksonXmlProperty(isAttribute = true) val isbn: String)
