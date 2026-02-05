package io.micronaut.xml.jackson.docs;

// 1. Update this import to Jackson 3
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
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

    @JsonCreator
    public BookSaved(
        @JsonProperty("name") String name,
        @JsonProperty("isbn") String isbn) {
        this.name = name;
        this.isbn = isbn;
    }

    @NonNull
    public String getName() { return name; }

    @NonNull
    public String getIsbn() { return isbn; }
}
