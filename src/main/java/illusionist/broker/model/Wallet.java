package illusionist.broker.model;

import illusionist.broker.api.RestApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet implements RestApiResponse {
  UUID accountId;
  UUID walletId;
  Symbol symbol;
  BigDecimal available;
  BigDecimal locked;

  public Wallet addAvailable (BigDecimal amountToAdd) {
    return new Wallet(
      this.accountId,
      this.walletId,
      this.symbol,
      this.available.add(amountToAdd),
      this.locked
    );
  }
}
