from typing import Annotated

from micronaut.http import MediaType
from micronaut.http.annotation import Body, Consumes, Post, Produces
from micronaut.http.client.annotation import Client

from .Book import Book
from .BookSaved import BookSaved


@Client("/")
class BookClient:
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Post("/book")
    def save(self, book: Annotated[Book, Body]) -> BookSaved:
        ...
