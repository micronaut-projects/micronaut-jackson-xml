package io.micronaut.xml.jackson.docs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.core.annotation.Introspected;

@Introspected
@JacksonXmlRootElement(localName = "book")
public record Book(String name) {
}
