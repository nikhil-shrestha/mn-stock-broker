package illusionist.broker;

import com.fasterxml.jackson.databind.JsonNode;
import illusionist.broker.model.Symbol;
import illusionist.broker.model.WatchList;
import illusionist.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class WatchListControllerTest {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
  private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

  @Inject
  @Client("/account/watchlist")
  HttpClient client;

  @Inject
  InMemoryAccountStore store;

  @BeforeEach
  void setup(){
    store.deleteWatchList(TEST_ACCOUNT_ID);
  }

  @Test
  void returnsEmptyWatchListForTestAccount() {
    var result = client.toBlocking().retrieve(HttpRequest.GET("/"), WatchList.class);
    assertNull(result.getSymbols());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  @Test
  void returnsWatchListForAccount() {
    store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
      Stream.of("AAPL", "GOOGL", "MSFT")
        .map(Symbol::new)
        .collect(Collectors.toList())
    ));

    var response = client.toBlocking().exchange("/", JsonNode.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(3, response.getBody().get().size());
  }


  @Test
  void canUpdateWatchListForAccount() {
    var symbols =  Stream.of("AAPL", "GOOGL", "MSFT")
      .map(Symbol::new)
      .collect(Collectors.toList());

    final var request = HttpRequest.PUT("/", new WatchList(symbols))
      .accept(MediaType.APPLICATION_JSON);
    final HttpResponse<Object> added = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(symbols, store.getWatchList(TEST_ACCOUNT_ID).getSymbols());
  }
}
