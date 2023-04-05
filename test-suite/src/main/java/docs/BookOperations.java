package docs;

import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.reactivestreams.Publisher;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public interface BookOperations {
    @Post
    @SingleResult
    Publisher<BookSaved> save(@Valid @Body Book book);

    @Get("/{isbn}")
    @SingleResult
    Publisher<BookSaved> find(@NotEmpty String isbn);
}
