package io.micronaut.xml.jackson.docs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

@JacksonXmlRootElement(localName = "book")
@Introspected
public class BookSaved {

    @NonNull
    private final String name;

    @NonNull
    @JacksonXmlProperty(isAttribute = true)
    private final String isbn;

    public BookSaved(String name, String isbn) {
        this.name = name;
        this.isbn = isbn;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getIsbn() {
        return isbn;
    }
}
