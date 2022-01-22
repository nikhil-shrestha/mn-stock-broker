package illusionist.broker;

import illusionist.broker.error.CustomError;
import illusionist.broker.model.Quote;
import illusionist.broker.persistence.jpa.QuoteDTO;
import illusionist.broker.persistence.jpa.QuotesRepository;
import illusionist.broker.persistence.model.QuoteEntity;
import illusionist.broker.persistence.model.SymbolEntity;
import illusionist.broker.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quotes")
public class QuotesController {

  private final InMemoryStore store;
  private final QuotesRepository quotes;

  public QuotesController(final InMemoryStore store, QuotesRepository quotes) {
    this.store = store;
    this.quotes = quotes;
  }

  @Operation(summary = "Returns a quote for the given symbol.")
  @ApiResponse(
    content = @Content(mediaType = MediaType.APPLICATION_JSON)
  )
  @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
  @Tag(name = "quotes")
  @Get("/{symbol}")
  public HttpResponse getQuotes(@PathVariable String symbol) {
    final Optional<Quote> maybeQuote = store.fetchQuote(symbol);
    if (maybeQuote.isEmpty()) {
      final CustomError notFound = CustomError.builder()
        .status(HttpStatus.NOT_FOUND.getCode())
        .error(HttpStatus.NOT_FOUND.name())
        .message("quote for symbol not available")
        .build();
      return HttpResponse.notFound(notFound);
    }
    return HttpResponse.ok(maybeQuote.get());
  }

  @Operation(summary = "Returns a quote for the given symbol.")
  @ApiResponse(
    content = @Content(mediaType = MediaType.APPLICATION_JSON)
  )
  @Tag(name = "quotes")
  @Get("/jpa")
  public List<QuoteEntity> getAllQuotesViaJPA() {
    return quotes.findAll();
  }

  @Operation(summary = "Returns a quote for the given symbol. Fetched from the database via JPA")
  @ApiResponse(
    content = @Content(mediaType = MediaType.APPLICATION_JSON)
  )
  @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
  @Tag(name = "quotes")
  @Get("/{symbol}/jpa")
  public HttpResponse getQuotesViaJPA(@PathVariable String symbol) {
    final Optional<QuoteEntity> maybeQuote = quotes.findBySymbolValue(symbol);
    if (maybeQuote.isEmpty()) {
      final CustomError notFound = CustomError.builder()
        .status(HttpStatus.NOT_FOUND.getCode())
        .error(HttpStatus.NOT_FOUND.name())
        .message("quote for symbol not available in db")
        .build();
      return HttpResponse.notFound(notFound);
    }
    return HttpResponse.ok(maybeQuote.get());
  }

  @Tag(name = "quotes")
  @Get("/jpa/ordered/desc")
  public List<QuoteDTO> orderedDesc() {
    return quotes.listOrderByVolumeDesc();
  }

  @Tag(name = "quotes")
  @Get("/jpa/ordered/asc")
  public List<QuoteDTO> orderedAsc() {
    return quotes.listOrderByVolumeAsc();
  }

  @Tag(name = "quotes")
  @Get("/jpa/volume/{volume}/{order}")
  public List<QuoteDTO> volumeFilter(@PathVariable BigDecimal volume, @PathVariable String order) {
    return quotes.findByVolumeGreaterThanOrderByVolume(volume, order);
  }

}
