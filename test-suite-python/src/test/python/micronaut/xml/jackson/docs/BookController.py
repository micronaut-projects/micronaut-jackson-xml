from typing import Annotated
from uuid import uuid4

from micronaut.http import MediaType
from micronaut.http.annotation import Body, Consumes, Controller, Post, Produces

from .Book import Book
from .BookSaved import BookSaved


@Controller
class BookController:

    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Post("/book")
    def save(self, book: Annotated[Book, Body]) -> BookSaved:
        return BookSaved(book.name, str(uuid4()))
