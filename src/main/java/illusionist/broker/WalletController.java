package illusionist.broker;

import illusionist.broker.error.CustomError;
import illusionist.broker.model.DepositFiatMoney;
import illusionist.broker.model.Wallet;
import illusionist.broker.model.WithdrawFiatMoney;
import illusionist.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.Collection;
import java.util.List;

@Controller("/account/wallets")
public class WalletController {

  private final InMemoryAccountStore store;

  public WalletController(InMemoryAccountStore memoryStore) {
    this.store = memoryStore;
  }


  public static final List<String> SUPPORTED_FIAT_CURRENCIES = List.of("EUR", "USD", "CHF", "GBP");

  @Get(produces = MediaType.APPLICATION_JSON)
  public Collection<Wallet> get() {
    return store.getWallets(InMemoryAccountStore.ACCOUNT_ID);
  }

  @Post(
    value = "/deposit",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON
  )
  public HttpResponse<CustomError> depositFiatMoney(@Body DepositFiatMoney deposit) {
    // Option 1: Custom HttpResponse
    if (!SUPPORTED_FIAT_CURRENCIES.contains(deposit.getSymbol().getValue())) {
      return HttpResponse.badRequest()
        .body(new CustomError(
          HttpStatus.BAD_REQUEST.getCode(),
          "UNSUPPORTED_FIAT_CURRENCIES",
          String.format("Only %s are supported", SUPPORTED_FIAT_CURRENCIES)
        ));
    }


    return HttpResponse.ok();
  }

  @Post(
    value = "/withdraw",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON
  )
  public void withdrawFiatMoney(@Body WithdrawFiatMoney withdraw) {
    // Option 2: Custom Error Processing
  }

}
