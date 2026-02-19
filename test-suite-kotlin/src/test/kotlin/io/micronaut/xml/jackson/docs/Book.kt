package io.micronaut.xml.jackson.docs

import io.micronaut.core.annotation.Introspected
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@Introspected
@JacksonXmlRootElement(localName = "book")
data class Book(val name: String)
