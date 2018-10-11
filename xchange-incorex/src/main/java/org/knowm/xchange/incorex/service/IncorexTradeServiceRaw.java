package org.knowm.xchange.incorex.service;

import static org.apache.commons.lang3.StringUtils.join;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.incorex.dto.trade.IncorexUserTrades;
import org.knowm.xchange.utils.DateUtils;

public class IncorexTradeServiceRaw extends BaseIncorexService {
  protected IncorexTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  protected String placeOrder(
      String type, BigDecimal price, CurrencyPair currencyPair, BigDecimal originalAmount) {
    Map map =
        incorex.orderCreate(
            signatureCreator,
            apiKey,
            exchange.getNonceFactory(),
            IncorexAdapters.format(currencyPair),
            originalAmount,
            price,
            type);

    Boolean result = (Boolean) map.get("result");
    if (!result) throw new ExchangeException("Failed to place order: " + map.get("error"));

    return map.get("order_id").toString();
  }

  public List<LimitOrder> openOrders() {
    Map<String, List<Map<String, String>>> map =
        incorex.userOpenOrders(signatureCreator, apiKey, exchange.getNonceFactory());

    List<LimitOrder> openOrders = new ArrayList<>();

    for (String market : map.keySet()) {

      CurrencyPair currencyPair = adaptMarket(market);

      for (Map<String, String> order : map.get(market)) {
        Order.OrderType type = IncorexAdapters.adaptOrderType(order);
        BigDecimal amount = new BigDecimal(order.get("quantity"));
        String id = order.get("order_id");
        BigDecimal price = new BigDecimal(order.get("price"));
        Date created = DateUtils.fromUnixTime(Long.valueOf(order.get("created")));

        openOrders.add(new LimitOrder(type, amount, currencyPair, id, created, price));
      }
    }
    return openOrders;
  }

  public IncorexUserTrades userTrades(String orderId) {
    Map<String, Object> map =
        incorex.orderTrades(signatureCreator, apiKey, exchange.getNonceFactory(), orderId);

    List<UserTrade> userTrades = new ArrayList<>();

    Boolean result = (Boolean) map.get("result");
    if (result != null && !result) return null;

    BigDecimal originalAmount = new BigDecimal(map.get("out_amount").toString());

    for (Map<String, Object> tradeDatum : (List<Map<String, Object>>) map.get("trades")) {
      CurrencyPair market = adaptMarket(tradeDatum.get("pair").toString());
      Map<String, String> bodge = new HashMap<>();
      for (String key : tradeDatum.keySet()) {
        bodge.put(key, tradeDatum.get(key).toString());
      }
      userTrades.add(IncorexAdapters.adaptTrade(bodge, market));
    }

    return new IncorexUserTrades(originalAmount, userTrades);
  }

  public List<UserTrade> trades(
      Integer limit, Long offset, Collection<CurrencyPair> currencyPairs) {
    List<String> markets = new ArrayList<>();
    for (CurrencyPair currencyPair : currencyPairs) {
      markets.add(IncorexAdapters.format(currencyPair));
    }

    Map<String, List<Map<String, String>>> map =
        incorex.userTrades(
            signatureCreator,
            apiKey,
            exchange.getNonceFactory(),
            join(markets, ","),
            offset,
            limit);

    List<UserTrade> trades = new ArrayList<>();
    for (String market : map.keySet()) {
      for (Map<String, String> tradeDatum : map.get(market)) {
        trades.add(IncorexAdapters.adaptTrade(tradeDatum, adaptMarket(market)));
      }
    }

    Collections.sort(trades, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
    return trades;
  }
}
