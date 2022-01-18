package illusionist.broker;

import illusionist.broker.model.WatchList;
import illusionist.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListController.class);

  private final InMemoryAccountStore store;
  static final UUID ACCOUNT_ID = UUID.randomUUID();

  public WatchListController(InMemoryAccountStore memoryStore) {
    this.store = memoryStore;
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  public WatchList get(){
    LOG.debug("get - {}", Thread.currentThread().getName());
    return store.getWatchList(ACCOUNT_ID);
  }

  @Put(
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON
  )
  public WatchList update(@Body WatchList watchList){
    return store.updateWatchList(ACCOUNT_ID, watchList);
  }

}
