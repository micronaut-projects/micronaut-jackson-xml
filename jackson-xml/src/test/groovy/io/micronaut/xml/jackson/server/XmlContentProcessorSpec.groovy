/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.xml.jackson.server


import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

class XmlContentProcessorSpec extends Specification {

    @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [:])

    void "test sending a single book"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        List<Book> books = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream", '<book><title>First Book</title></book>')
                        .contentType(MediaType.TEXT_XML_TYPE), Argument.listOf(Book.class))

        then:
        books.size() == 1
        books[0].title == "First Book"
    }

    void "test sending a list of books"() {
        given:
        StreamingHttpClient client = embeddedServer.applicationContext.createBean(StreamingHttpClient, embeddedServer.getURL())

        when:
        List<Book> books = Flux.from(client.jsonStream(
                HttpRequest.POST("/xml/stream/list", '<books><book><title>First Book</title></book><book><title>Second Book</title></book></books>')
                        .contentType(MediaType.TEXT_XML_TYPE), Book.class)).collectList().block()

        then:
        books.size() == 2
        books[0].title == "First Book"
        books[1].title == "Second Book"
    }

    void "test sending a blocking author"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        Author author = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream/author", '<author name="Joe"><books><book><title>First Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE), Author.class)

        then:
        author.books.size() == 1
        author.books[0].title == "First Book"
        author.name == "Joe"
    }

    void "test sending a blocking author with 2 books"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        Author author = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream/author", '<author name="Joe"><books><book><title>First Book</title></book><book><title>Second Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE), Author.class)

        then:
        author.books.size() == 2
        author.books[0].title == "First Book"
        author.books[1].title == "Second Book"
        author.name == "Joe"
    }

    void "test mapping xml single primitive field to controller param"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        String name = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream/author/name", '<author name="Joe"><books><book><title>First Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE))

        then:
        name == 'Joe'
    }

    void "test mapping xml single complex field to controller param"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        Book book = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream/author/book", '<author name="Joe"><book><title>First Book</title></book></author>')
                        .contentType(MediaType.TEXT_XML_TYPE), Book)

        then:
        book.title == "First Book"
    }

    void "test mapping xml list field to controller param"() {
        given:
        StreamingHttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        List<Book> books = Flux.from(client.jsonStream(
                HttpRequest.POST("/xml/stream/author/books", '<author name="Joe"><books><book><title>First Book</title></book><book><title>Second Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE))).collectList().block()

        then:
        books.size() == 2
        books[0].title == "First Book"
        books[1].title == "Second Book"
    }

    void "test binding xml set field to controller param"() {
        given:
        StreamingHttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        Set<String> books = Flux.from(client.jsonStream(
                HttpRequest.POST("/xml/stream/author/bookSet", '<author name="Joe"><books><book><title>First Book</title></book><book><title>Second Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE)))
                .collectList()
                .block()
                .stream()
                .map{book -> book.title}
                .collect(Collectors.toSet())

        then:
        books.size() == 2
        books.contains "First Book"
        books.contains "Second Book"
    }

    void "test binding two fields to controller parameters"() {
        given:
        StreamingHttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        Author author = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream/author/argumentBinding/2", '<author name="Joe"><books><book><title>First Book</title></book><book><title>Second Book</title></book></books></author>')
                        .contentType(MediaType.TEXT_XML_TYPE), Author)

        then:
        author.name == "Joe"
        author.books.size() == 2
    }

    void "test sending a big book"() {
        HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

        when:
        List<Book> books = client.toBlocking().retrieve(
                HttpRequest.POST("/xml/stream", '<book><title>' + "a"*5000 + '</title></book>')
                        .contentType(MediaType.TEXT_XML_TYPE), Argument.listOf(Book.class))

        then:
        books.size() == 1
        books[0].title.size() == 5000
    }

    @Controller(value = "/xml/stream", consumes = MediaType.TEXT_XML)
    static class StreamController {

        @Post
        Publisher<Book> stream(@Body Publisher<Book> books) {
            return books
        }

        @Post("/list")
        Publisher<Book> streamList(@Body Publisher<List<Book>> books) {
            return Flux.from(books).flatMap({ bookList ->
                return Flux.fromIterable(bookList)
            })
        }

        @Post("/author")
        Author author(@Body Author author) {
            return author
        }

        @Post("/author/argumentBinding/2")
        Author author(String name, List<Book> books) {
            def author = new Author()
            author.name = name
            author.books = books
            return author
        }

        @Post("/author/name")
        String author(String name) {
            return name
        }

        @Produces(MediaType.TEXT_XML)
        @Post("/author/book")
        Book bookList(Book book) {
            return book
        }

        @Post("/author/books")
        Publisher<Book> bookList(List<Book> books) {
            return Flux.fromIterable(books)
        }

        @Post("/author/bookSet")
        Publisher<Book> bookList(Set<Book> books) {
            return Flux.fromIterable(books)
        }
    }

    static class Book {
        String title
    }

    static class Author {
        String name
        List<Book> books
        Book book
    }
}
