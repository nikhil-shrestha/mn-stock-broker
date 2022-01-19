package illusionist.broker.store;

import com.github.javafaker.Faker;
import illusionist.broker.model.Quote;
import illusionist.broker.model.Symbol;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {

  private final List<Symbol> symbols = new ArrayList<>();
  private final Map<String, Quote> cachedQuotes = new HashMap<>();

  private static final ThreadLocalRandom localRandom = ThreadLocalRandom.current();

  @EventListener
  public void onStartup(StartupEvent startupEvent) {
    symbols.addAll(Stream.of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")
      .map(Symbol::new)
      .collect(Collectors.toList())
    );
    symbols.forEach(symbol ->
      cachedQuotes.put(symbol.getValue(), initRandomQuote(symbol))
    );
  }

  public List<Symbol> getAllSymbols() {
    return symbols;
  }

  public Optional<Quote> fetchQuote(String symbol) {
    return Optional.ofNullable(cachedQuotes.get(symbol));
  }

  public void update(final Quote quote) {
    cachedQuotes.put(quote.getSymbol().getValue(), quote);
  }

  public Quote initRandomQuote(final Symbol symbol) {
    return Quote.builder().symbol(symbol).bid(randomValue())
      .ask(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private BigDecimal randomValue() {
    return BigDecimal.valueOf(localRandom.nextDouble(1, 100));
  }
}
