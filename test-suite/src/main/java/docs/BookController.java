package docs;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Controller("/book")
public class BookController implements BookOperations {

    public Publisher<BookSaved> save(@Valid @Body Book book) {
        BookSaved bookSaved = new BookSaved();
        bookSaved.setName(book.getName());
        bookSaved.setIsbn(UUID.randomUUID().toString());
        return Mono.justOrEmpty(bookSaved);
    }

    public Publisher<BookSaved> find(@NotEmpty String isbn) {
        BookSaved book = new BookSaved();
        book.setName("Tom Sayer");
        book.setIsbn(isbn);
        return Mono.justOrEmpty(book);
    }
}
