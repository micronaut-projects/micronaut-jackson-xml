package io.micronaut.xml.jackson.graal;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class BookControllerTest {

    @Inject
    BookClient client;

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testSavebook() throws DatatypeConfigurationException {

        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(2023, 12, 10, 23, 45, 10, 234, 0);

        Book book = new Book("Huckleberry Finn", date);
        BookSaved result = client.save(book);
        assertNotNull(result);
        assertEquals("Huckleberry Finn", result.getName());
        assertTrue(StringUtils.isNotEmpty(result.getIsbn()));
        assertEquals(date, result.getDate());

        String xml = httpClient.toBlocking().retrieve(
            HttpRequest.POST("/book", "<book><name>Huckleberry Finn</name><date>" + date + "</date></book>")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML)
        );

        assertNotNull(xml);
        assertTrue(xml.startsWith("<book isbn=\""));
        assertTrue(xml.endsWith("\"><name>Huckleberry Finn</name><date>" + date + "</date></book>"));
    }
}
