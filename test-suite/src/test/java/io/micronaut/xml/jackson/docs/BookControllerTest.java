package io.micronaut.xml.jackson.docs;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class BookControllerTest {
    @Inject
    @Client("/books")
    BookClient client;

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testSavebook() {
        Book book = new Book("Huckleberry Finn");

        BookSaved result = client.save(book);

        assertNotNull(result);
        assertEquals("Huckleberry Finn", result.getName());
        assertTrue(StringUtils.isNotEmpty(result.getIsbn()));

        String xml = httpClient.toBlocking().retrieve(HttpRequest.POST("/book", "<book><name>Huckleberry Finn</name></book>")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML));

        assertTrue(xml.startsWith("<book isbn=\""));
        assertTrue(xml.endsWith("\"><name>Huckleberry Finn</name></book>"));
    }
}

