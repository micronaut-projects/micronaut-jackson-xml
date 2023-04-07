package io.micronaut.xml.jackson.docs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

@Introspected
@JacksonXmlRootElement(localName = "book")
public class Book {
    @NonNull
    private final String name;

    public Book(String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }
}
