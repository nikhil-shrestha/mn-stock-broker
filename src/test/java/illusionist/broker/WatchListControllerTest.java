package illusionist.broker;

import com.fasterxml.jackson.databind.JsonNode;
import illusionist.broker.model.Symbol;
import illusionist.broker.model.WatchList;
import illusionist.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.rxjava2.http.client.RxHttpClient;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
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
  public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

  @Inject
  @Client("/")
  RxHttpClient client;

  @Inject
  InMemoryAccountStore store;

  @BeforeEach
  void setup() {
    store.deleteWatchList(TEST_ACCOUNT_ID);
  }

  @Test
  void unauthorizedAccessIsForbidden() {
    try {
      client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
      fail("Should fail if no exception is thrown");
    } catch (HttpClientResponseException e) {
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
    }
  }

  @Test
  void returnsEmptyWatchListForTestAccount() {
    final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

    var request = HttpRequest.GET(ACCOUNT_WATCHLIST)
      .accept(MediaType.APPLICATION_JSON)
      .bearerAuth(token.getAccessToken());

    var result = client.toBlocking().retrieve(request, WatchList.class);
    assertNull(result.getSymbols());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  @Test
  void returnsWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

    givenWatchListForAccountExists();

    var request = HttpRequest.GET(ACCOUNT_WATCHLIST)
      .accept(MediaType.APPLICATION_JSON)
      .bearerAuth(token.getAccessToken());

    var response = client.toBlocking().exchange(request, JsonNode.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(3, response.getBody().get().size());
  }

  @Test
  void canUpdateWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

    var symbols = Stream.of("AAPL", "GOOGL", "MSFT")
      .map(Symbol::new)
      .collect(Collectors.toList());

    final var request = HttpRequest.PUT(ACCOUNT_WATCHLIST, new WatchList(symbols))
      .accept(MediaType.APPLICATION_JSON)
      .bearerAuth(token.getAccessToken());
    final HttpResponse<Object> added = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(symbols, store.getWatchList(TEST_ACCOUNT_ID).getSymbols());
  }

  @Test
  void canDeleteWatchListForAccount() {
    final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

    givenWatchListForAccountExists();
    assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    var request = HttpRequest.DELETE(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID)
      .accept(MediaType.APPLICATION_JSON)
      .bearerAuth(token.getAccessToken());
    final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, deleted.getStatus());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  private void givenWatchListForAccountExists() {
    store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
      Stream.of("AAPL", "GOOGL", "MSFT")
        .map(Symbol::new)
        .collect(Collectors.toList())
    ));
  }

  private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
    final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
    var login = HttpRequest.POST("/login", credentials);
    var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    final BearerAccessRefreshToken token = response.body();
    assertNotNull(token);
    assertEquals("my-user", token.getUsername());
    LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());
    return token;
  }
}
