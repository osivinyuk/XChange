package org.knowm.xchange.incorex;

import java.io.IOException;
import java.io.InputStream;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.incorex.service.IncorexAccountService;
import org.knowm.xchange.incorex.service.IncorexMarketDataService;
import org.knowm.xchange.incorex.service.IncorexTradeService;
import org.knowm.xchange.utils.nonce.CurrentNanosecondTimeIncrementalNonceFactory;
import si.mazi.rescu.SynchronizedValueFactory;

public class IncorexExchange extends BaseExchange implements Exchange {
  private SynchronizedValueFactory<Long> nonceFactory =
      new CurrentNanosecondTimeIncrementalNonceFactory();

  @Override
  protected void initServices() {
    this.marketDataService = new IncorexMarketDataService(this);
    this.accountService = new IncorexAccountService(this);
    this.tradeService = new IncorexTradeService(this);
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {
    return nonceFactory;
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification exchangeSpecification =
        new ExchangeSpecification(this.getClass().getCanonicalName());
    exchangeSpecification.setShouldLoadRemoteMetaData(false);
    exchangeSpecification.setSslUri("https://api.incorex.com");
    exchangeSpecification.setHost("incorex.com");
    exchangeSpecification.setPort(80);
    exchangeSpecification.setExchangeName("incorex");
    exchangeSpecification.setExchangeDescription("incorex");
    return exchangeSpecification;
  }

  @Override
  public void remoteInit() throws IOException, ExchangeException {
    ((IncorexMarketDataService) marketDataService).updateMetadata(exchangeMetaData);
  }

  @Override
  protected void loadExchangeMetaData(InputStream is) {
    exchangeMetaData = loadMetaData(is, ExchangeMetaData.class);
  }
}
