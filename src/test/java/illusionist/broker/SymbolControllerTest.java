package illusionist.broker;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SymbolControllerTest {

  @Inject
  @Client("/symbols")
  HttpClient client;

  @Inject
  InMemoryStore inMemoryStore;

  @BeforeEach
  void setup(){
    inMemoryStore.initializeWith(10);
  }

  @Test
  void symbolsEmdpointReturnsListOfSymbol() {
    var response = client.toBlocking().exchange("/", JsonNode.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(10, response.getBody().get().size());
  }

  @Test
  void symbolsEmdpointReturnsTheCorrectSymbol() {
    var testSymbol = new Symbol("TEST");
    inMemoryStore.getSymbols().put(testSymbol.getValue(), testSymbol);
    var response = client.toBlocking().exchange("/"+testSymbol.getValue(), Symbol.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(testSymbol, response.getBody().get());
  }

}
