package io.micronaut.xml.jackson.docs

import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

@MicronautTest
class BookControllerTest {
    @Inject
    lateinit var client: BookClient

    @Inject
    @field:Client("/")
    lateinit var httpClient : HttpClient

    @Test
    fun testSavebook() {
        val book = Book("Huckleberry Finn")
        val result = client.save(book)
        Assertions.assertNotNull(result)
        Assertions.assertEquals("Huckleberry Finn", result.name)
        Assertions.assertTrue(StringUtils.isNotEmpty(result.isbn))

        val xml = httpClient.toBlocking().retrieve(
            HttpRequest.POST("/book", "<book><name>Huckleberry Finn</name></book>")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML)
        )

        assertNotNull(xml)
        Assertions.assertTrue(xml.startsWith("<book isbn=\""))
        Assertions.assertTrue(xml.endsWith("\"><name>Huckleberry Finn</name></book>"))
    }
}
