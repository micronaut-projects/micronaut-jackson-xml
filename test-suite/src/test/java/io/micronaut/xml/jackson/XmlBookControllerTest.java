package io.micronaut.xml.jackson;

import docs.BookSaved;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlBookControllerTest {

    @Test
    void testGetBook() {
        int mockPort = SocketUtils.findAvailableTcpPort();

        ApplicationContext.run(EmbeddedServer.class, PropertySource.mapOf(
            "spec.name", "XmlBookControllerTest",
            "micronaut.server.port", mockPort
        ));
        ApplicationContext applicationContext = ApplicationContext.run(PropertySource.mapOf(
            "spec.name", "XmlBookControllerTest",
            "mock.url", "http://localhost:" + mockPort
        ));
        XmlClient xmlClient = applicationContext.getBean(XmlClient.class);
        BookSaved bookSaved = Mono.from(xmlClient.getXmlContent()).block();

        assertNotNull(bookSaved);
        assertEquals("Huckleberry Finn", bookSaved.getName());
        assertFalse(bookSaved.getIsbn().isEmpty());
    }

    @Test
    void testPostBook() {
        int mockPort = SocketUtils.findAvailableTcpPort();
        ApplicationContext.run(EmbeddedServer.class, PropertySource.mapOf(
            "spec.name", "XmlBookControllerTest",
            "micronaut.server.port", mockPort
        ));
        ApplicationContext applicationContext = ApplicationContext.run(PropertySource.mapOf(
            "spec.name", "XmlBookControllerTest",
            "mock.url", "http://localhost:" + mockPort
        ));
        BookSaved book = new BookSaved();
        book.setName("Tom Sayer");
        book.setIsbn(UUID.randomUUID().toString());

        XmlClient xmlClient = applicationContext.getBean(XmlClient.class);
        BookSaved bookSaved = Mono.from(xmlClient.putXmlContent(book.toString())).block();

        assertNotNull(bookSaved);
        assertEquals("Tom Sayer", bookSaved.getName());
        assertFalse(bookSaved.getIsbn().isEmpty());
    }

    @Requires(property = "spec.name", value = "XmlBookControllerTest")
    @Client(value = "${mock.url}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    interface XmlClient {
        @Get
        @SingleResult
        Publisher<BookSaved> getXmlContent();

        @Post
        @SingleResult
        Publisher<BookSaved> putXmlContent(@Body @NotEmpty String xml);
    }

    @Requires(property = "spec.name", value = "XmlBookControllerTest")
    @Controller
    static class MockController {

        @Consumes(MediaType.APPLICATION_XML)
        @Produces(MediaType.APPLICATION_XML)
        @Post
        public String save(BookSaved bookSaved) {
            return bookSaved.toString();
        }

        @Consumes(MediaType.APPLICATION_XML)
        @Produces(MediaType.APPLICATION_XML)
        @Get
        public String get() {
            BookSaved book = new BookSaved();
            book.setName("Huckleberry Finn");
            book.setIsbn(UUID.randomUUID().toString());
            return book.toString();
        }
    }
}

