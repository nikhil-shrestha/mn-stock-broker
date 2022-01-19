package illusionist.broker.store;


import illusionist.broker.model.DepositFiatMoney;
import illusionist.broker.model.Wallet;
import illusionist.broker.model.WatchList;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.*;

@Singleton
public class InMemoryAccountStore {

  public static final UUID ACCOUNT_ID = UUID.fromString("d73758a8-78d9-11ec-90d6-0242ac120003");
  // make sure to return proper map as type
  private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();
  private final HashMap<UUID, Map<UUID, Wallet>> walletsPerAccount = new HashMap<>();

  public WatchList getWatchList(final UUID accountId) {
    return watchListsPerAccount.getOrDefault(accountId, new WatchList());
  }

  public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
    watchListsPerAccount.put(accountId, watchList);
    return getWatchList(accountId);
  }

  public void deleteWatchList(final UUID accountId) {
    watchListsPerAccount.remove(accountId);
  }

  public Collection<Wallet> getWallets(UUID accountId) {
    return Optional.ofNullable(walletsPerAccount.get(accountId))
      .orElse(new HashMap<>())
      .values();
  }

  public Wallet depositToWallet(DepositFiatMoney deposit) {

    final var wallets = Optional.ofNullable(
      walletsPerAccount.get(deposit.getAccountId())
    ).orElse(
      new HashMap<>()
    );

    var oldWallet = Optional.ofNullable(wallets.get(deposit.getWalletId()))
      .orElse(
        new Wallet(ACCOUNT_ID, deposit.getWalletId(), deposit.getSymbol(), BigDecimal.ZERO, BigDecimal.ZERO)
      );
    var newWallet = oldWallet.addAvailable(deposit.getAmount());

    // Update wallet in store
    wallets.put(newWallet.getWalletId(), newWallet);
    walletsPerAccount.put(newWallet.getAccountId(), wallets);
    return newWallet;
  }
}
