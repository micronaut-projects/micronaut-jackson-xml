package io.micronaut.xml.jackson.docs;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import javax.validation.Valid;

@Controller
class BookController {

    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Post("/book")
    BookSaved save(@Valid @Body Book book) {
        new BookSaved(name: book.getName(), isbn: UUID.randomUUID().toString())
    }
}
