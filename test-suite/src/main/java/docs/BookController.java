package docs;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Controller("/book")
public class BookController implements BookOperations {

    public BookSaved save(@Valid @Body Book book) {
        BookSaved bookSaved = new BookSaved();
        bookSaved.setName(book.getName());
        bookSaved.setIsbn(UUID.randomUUID().toString());
        return bookSaved;
    }

    public BookSaved find(@NotEmpty String isbn) {
        BookSaved book = new BookSaved();
        book.setName("Mark Twain");
        book.setIsbn(isbn);
        return book;
    }
}
