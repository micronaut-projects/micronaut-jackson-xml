package io.micronaut.xml.jackson.graal;

import java.util.UUID;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

@Controller
public class BookController {

    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Post("/book")
    public BookSaved save(@Body Book book) {
        return new BookSaved(book.getName(), UUID.randomUUID().toString(), book.getDate());
    }
}
