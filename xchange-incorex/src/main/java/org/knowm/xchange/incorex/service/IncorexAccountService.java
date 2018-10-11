package org.knowm.xchange.incorex.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.incorex.IncorexExchange;
import org.knowm.xchange.incorex.dto.account.IncorexFundingHistoryParams;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.DefaultWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.RippleWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public class IncorexAccountService extends IncorexAccountServiceRaw implements AccountService {
  public IncorexAccountService(IncorexExchange incorexExchange) {
    super(incorexExchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    return new AccountInfo(new Wallet(balances()));
  }

  @Override
  public String requestDepositAddress(Currency currency, String... args) throws IOException {
    return depositAddresses().get(currency.getCurrencyCode());
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException {
    Map<String, Object> result;

    if (params instanceof RippleWithdrawFundsParams) {
      RippleWithdrawFundsParams rippleWithdrawFundsParams = (RippleWithdrawFundsParams) params;
      result =
          incorex.withdrawCrypt(
              signatureCreator,
              apiKey,
              exchange.getNonceFactory(),
              rippleWithdrawFundsParams.getAmount(),
              rippleWithdrawFundsParams.getCurrency().getCurrencyCode(),
              rippleWithdrawFundsParams.getAddress(),
              rippleWithdrawFundsParams.getTag());
    } else if (params instanceof DefaultWithdrawFundsParams) {
      DefaultWithdrawFundsParams defaultWithdrawFundsParams = (DefaultWithdrawFundsParams) params;
      result =
          incorex.withdrawCrypt(
              signatureCreator,
              apiKey,
              exchange.getNonceFactory(),
              defaultWithdrawFundsParams.getAmount(),
              defaultWithdrawFundsParams.getCurrency().getCurrencyCode(),
              defaultWithdrawFundsParams.getAddress(),
              null);
    } else {
      throw new IllegalStateException("Don't understand " + params);
    }

    Boolean success = (Boolean) result.get("result");
    if (success) {
      return result.get("task_id").toString();
    } else {
      throw new ExchangeException("Withdrawal failed: " + result.get("error").toString());
    }
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
    Date since = null;
    if (params instanceof IncorexFundingHistoryParams) {
      IncorexFundingHistoryParams thp = (IncorexFundingHistoryParams) params;
      since = thp.getDay();
    }
    return getFundingHistory(since);
  }
}
