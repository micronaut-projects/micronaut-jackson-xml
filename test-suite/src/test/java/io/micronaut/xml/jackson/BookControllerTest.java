package io.micronaut.xml.jackson;

import docs.Book;
import docs.BookClient;
import docs.BookSaved;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class BookControllerTest {
    @Inject
    @Client("/books")
    BookClient client;

    @Test
    void testSavebook() {
        Book book = new Book();
        book.setName("Huckleberry Finn");

        BookSaved result = Mono.from(client.save(book)).block();

        assertNotNull(result);
        assertEquals("Huckleberry Finn", result.getName());
        assertFalse(result.getIsbn().isEmpty());
    }

    @Test
    void testGetbook() {
        String name = "foo";

        BookSaved result = Mono.from(client.find(name)).block();

        assertNotNull(result);
        assertEquals("Tom Sayer", result.getName());
        assertEquals("foo", result.getIsbn());
    }
}

