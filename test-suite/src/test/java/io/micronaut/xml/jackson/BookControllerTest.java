package io.micronaut.xml.jackson;

import docs.Book;
import docs.BookClient;
import docs.BookSaved;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

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

        BookSaved result = client.save(book);

        assertNotNull(result);
        assertEquals("Huckleberry Finn", result.getName());
        assertFalse(result.getIsbn().isBlank());
    }
}

