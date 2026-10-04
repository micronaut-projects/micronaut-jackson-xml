from typing import Annotated

from jakarta.inject import Inject
from micronaut.http import HttpRequest, MediaType
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test

from .Book import Book
from .BookClient import BookClient


@MicronautTest
class BookControllerTest:
    client: Annotated[BookClient, Inject]

    http_client: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_save_book(self):
        book = Book("Huckleberry Finn")

        result = self.client.save(book)

        assert result is not None
        assert result.name == "Huckleberry Finn"
        assert result.isbn

    @Test
    def test_save_book_xml_document(self):
        xml = self.http_client.toBlocking().retrieve(
            HttpRequest.POST("/book", "<book><name>Huckleberry Finn</name></book>")
            .accept(MediaType.APPLICATION_XML)
            .contentType(MediaType.APPLICATION_XML))

        assert xml.startswith('<book isbn="')
        assert xml.endswith('"><name>Huckleberry Finn</name></book>')
