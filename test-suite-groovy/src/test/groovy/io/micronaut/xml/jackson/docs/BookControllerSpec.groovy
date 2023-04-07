package io.micronaut.xml.jackson.docs

import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class BookControllerSpec extends Specification {
    @Inject
    @Client("/books")
    BookClient client

    @Inject
    @Client("/")
    HttpClient httpClient

    void "test savebook"() {
        given:
        Book book = new Book(name: "Huckleberry Finn")

        when:
        BookSaved result = client.save(book)

        then:
        result
        "Huckleberry Finn" == result.name
        StringUtils.isNotEmpty(result.isbn)

        when:
        String xml = httpClient.toBlocking().retrieve(HttpRequest.POST("/book", "<book><name>Huckleberry Finn</name></book>")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML))

        then:
        xml.startsWith("<book isbn=\"")
        xml.endsWith("\"><name>Huckleberry Finn</name></book>")

    }
}

