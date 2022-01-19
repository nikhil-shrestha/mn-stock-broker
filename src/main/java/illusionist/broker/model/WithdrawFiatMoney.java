package illusionist.broker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawFiatMoney {
  UUID accountId;
  UUID walletId;
  Symbol symbol;
  BigDecimal amount;
}
