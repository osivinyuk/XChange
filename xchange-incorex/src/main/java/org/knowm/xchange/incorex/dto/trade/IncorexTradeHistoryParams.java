package org.knowm.xchange.incorex.dto.trade;

import java.util.ArrayList;
import java.util.Collection;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.TradeHistoryParamLimit;
import org.knowm.xchange.service.trade.params.TradeHistoryParamOffset;

public class IncorexTradeHistoryParams implements TradeHistoryParamLimit, TradeHistoryParamOffset {
  private Integer limit = 1000;
  private Long offset = 0L;
  private Collection<CurrencyPair> currencyPairs = new ArrayList<>();

  public IncorexTradeHistoryParams() {}

  public IncorexTradeHistoryParams(
      Integer limit, Long offset, Collection<CurrencyPair> currencyPairs) {
    this.limit = limit;
    this.offset = offset;
    this.currencyPairs = currencyPairs;
  }

  public Collection<CurrencyPair> getCurrencyPairs() {
    return currencyPairs;
  }

  public void setCurrencyPairs(Collection<CurrencyPair> currencyPairs) {
    this.currencyPairs = currencyPairs;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @Override
  public Long getOffset() {
    return offset;
  }

  @Override
  public void setOffset(Long offset) {
    this.offset = offset;
  }
}
