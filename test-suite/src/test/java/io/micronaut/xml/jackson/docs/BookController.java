package io.micronaut.xml.jackson.docs;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import java.util.UUID;

@Controller
public class BookController {

    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Post("/book")
    public BookSaved save(@Body Book book) {
        return new BookSaved(book.name(), UUID.randomUUID().toString());
    }
}
