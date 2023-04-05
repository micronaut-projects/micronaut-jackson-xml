package docs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;

@Introspected
public class Book {

    @NonNull
    @NotBlank
    @JacksonXmlProperty(namespace = "Book", localName = "name")
    private String name;

//    @NonNull
//    @NotBlank
//    @JacksonXmlProperty(namespace = "book", localName = "isbn")
//    private String isbn;

    public Book() {
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

//    @NonNull
//    public String getIsbn() {
//        return isbn;
//    }
//
//    public void setIsbn(@NonNull String isbn) {
//        this.isbn = isbn;
//    }

}
