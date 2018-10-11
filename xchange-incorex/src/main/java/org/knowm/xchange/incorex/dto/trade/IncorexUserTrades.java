package org.knowm.xchange.incorex.dto.trade;

import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.dto.trade.UserTrade;

public class IncorexUserTrades {
  private final BigDecimal originalAmount;
  private final List<UserTrade> userTrades;

  public IncorexUserTrades(BigDecimal originalAmount, List<UserTrade> userTrades) {
    this.originalAmount = originalAmount;
    this.userTrades = userTrades;
  }

  public BigDecimal getOriginalAmount() {
    return originalAmount;
  }

  public List<UserTrade> getUserTrades() {
    return userTrades;
  }
}
