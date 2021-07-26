package com.j6crypto.engine;

import com.j6crypto.controller.to.ClientExchangeTo;
import com.j6crypto.engine.entity.Client;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.engine.entity.MasterDataKey.IdCoin;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.to.LoginReq;
import com.j6crypto.to.LoginRes;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.ProfitPercentageTpSetup;
import com.j6crypto.to.setup.ProfitReduceFromHighestSetup;
import com.j6crypto.to.setup.ReboundMartingaleSetup;
import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.j6crypto.engine.entity.ClientExchange.Exchange.BINANCE;
import static com.j6crypto.engine.entity.MasterDataKey.IdCoin.BNBBUSD;
import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;
import static com.j6crypto.to.Trade.OrderType.MARKET;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator.AND;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Period.MIN1;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.ProductType.SPOT;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.FINISH;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.PM;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EngineApiTest extends ApiTestBase {
  private static Logger logger = LoggerFactory.getLogger(EngineApiTest.class);

  public static String atoId;
  private static ClientExchangeTo clientExchangeTo, clientExchangeToDummy;
  private static AutoTradeOrderSetup autoTradeOrderSetup;

  @Test
  public void test1PostClient() {
    Client client = new Client();
    client.setName("j6crypto");
    ResponseEntity responseEntity = restTemplate.postForEntity(getClientApiPath(), client, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test2AuthLogin() {
    LoginReq loginReq = new LoginReq();
    LoginRes loginRes = restTemplate.postForObject(getAuthApiPath("login"), loginReq, LoginRes.class);
    Assertions.assertNotNull(loginRes.getAuthToken());
    authToken = loginRes.getAuthToken();
    logger.info("authToken=" + loginRes.getAuthToken());
  }

  @Test
  public void test3PostClientExchange() {
    ClientExchangeTo clientExchangeTo = new ClientExchangeTo();
    clientExchangeTo.setApiKey(API_KEY);
    clientExchangeTo.setSecretKey(SECRET_KEY);
    clientExchangeTo.setExchange(BINANCE);
    ResponseEntity responseEntity = restTemplate.postForEntity(getClientApiPath("exchange"), entity(clientExchangeTo), String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    clientExchangeTo = new ClientExchangeTo();
    clientExchangeTo.setApiKey(API_KEY);
    clientExchangeTo.setSecretKey(SECRET_KEY);
    clientExchangeTo.setExchange(ClientExchange.Exchange.DUMMY);
    responseEntity = restTemplate.postForEntity(getClientApiPath("exchange"), entity(clientExchangeTo), String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test4GetClientExchange() {
    ResponseEntity<ClientExchangeTo[]> clientExchangeTos =
      restTemplate.exchange(getClientApiPath("exchange"), GET, entity(), ClientExchangeTo[].class);

    clientExchangeTo = getClientExchange(clientExchangeTos.getBody(), BINANCE);
    clientExchangeToDummy = getClientExchange(clientExchangeTos.getBody(), ClientExchange.Exchange.DUMMY);
  }

  @NotNull
  private ClientExchangeTo getClientExchange(ClientExchangeTo[] clientExchangeTos, ClientExchange.Exchange exchange) {
    return Arrays.stream(clientExchangeTos).filter(ex -> exchange.equals(ex.getExchange())).findAny().get();
  }

  @Test
  public void test5PostAto() {
    autoTradeOrderSetup = getAtoReboundMartingaleProfitReduceFromHighest(BNBBUSD.name(), clientExchangeTo.getId());
    Integer id = postAto(autoTradeOrderSetup);
    atoId = id + "";
    Assertions.assertNotNull(id);
  }

  @Test
  @Ignore
  public void test6GetAto() throws InterruptedException {
    Thread.sleep(61000);
    ResponseEntity<AutoTradeOrder> autoTradeOrder = restTemplate.exchange(getEngineApiPath("ato/" + atoId), GET, entity(), AutoTradeOrder.class);
    ProfitPercentageTpSetup profitPercentageTpSetup = ((ProfitPercentageTpSetup) autoTradeOrderSetup.getStopStates().get(0));
    ProfitPercentageTpSetup profitPercentageTpSetupRet = ((ProfitPercentageTpSetup) autoTradeOrder.getBody().getStopStates().get(0));
    Assertions.assertEquals(profitPercentageTpSetup.getProfitPercentageTp(), profitPercentageTpSetupRet.getProfitPercentageTp());
    Assertions.assertEquals(PM, autoTradeOrder.getBody().getStatus());
    Assertions.assertEquals(((ReboundMartingaleState) autoTradeOrderSetup.getPmStates().get(0)).getTradeQty(),
      autoTradeOrder.getBody().getPositionQty());
    Assertions.assertNotNull(autoTradeOrder.getBody().getTotalCost());
    Assertions.assertNotEquals(BigDecimal.ZERO, autoTradeOrder.getBody().getTotalCost());
  }

  @Test
  public void test7DummyReboundMartingaleProfitReduceFromHighest() {
    Integer clientExchangeId = clientExchangeToDummy.getId();
//    Integer clientExchangeId = 26;
    AutoTradeOrderSetup atoSetup = getAtoReboundMartingaleProfitReduceFromHighest(IdCoin.DUMMY.name(), clientExchangeId);
    Integer atoId = postAto(atoSetup);
//    Integer atoId = 23;

    sendPrice("13145.74");//set starting coin price
    AutoTradeOrder atoExpected = getAtoExpected();
    assertAll(getAto(atoId), atoExpected);

    sendPriceAfterPercentage("90");
    assertAll(getAto(atoId), atoExpected);

    sendPriceAfterPercentage("100.51");
    atoExpected.getLastTrade().setPrice(new BigDecimal("11891.504946600000000000"));
    atoExpected.setTotalCost(new BigDecimal("5007.45"));
    atoExpected.setPositionQty(new BigDecimal("0.40"));
    assertAll(getAto(atoId), atoExpected);

    //logger.info("Count totalCost's cost price ", atoExpected.getTotalCost().divide(atoExpected.getPositionQty()).toPlainString());
    sendPrice("12518.625");//set coin price to totalCost's cost price
    sendPriceAfterPercentage("100.71");
    assertAll(getAto(atoId), atoExpected);

    sendPriceAfterPercentage("99.79");
    atoExpected.getLastTrade().setPrice(new BigDecimal("12581.031472301250000000"));
    atoExpected.getLastTrade().setLongShort(SHORT);
    atoExpected.getLastTrade().setQty(new BigDecimal("0.4000"));
    atoExpected.setStatus(FINISH);
    assertAll(getAto(atoId), atoExpected);

//    BinanceApiRestClient client = BinanceApiClientFactory.newInstance().newRestClient();
//    List<Candlestick> candlesticks = client.getCandlestickBars(BTCUSDT.name(), CandlestickInterval.ONE_MINUTE,
//      1000, new Date().getTime() - 10000000, new Date().getTime());
//    candlesticks.forEach(c ->
//      sendMonitor(
//        MasterDataKey.IdCoin.DUMMY.name(),
//        LocalDateTime.ofInstant(ofEpochMilli(c.getCloseTime()), ZoneId.systemDefault()),
//        new BigDecimal(c.getClose())));
  }

  @NotNull
  private AutoTradeOrder getAtoExpected() {
    AutoTradeOrder atoExpected = new AutoTradeOrder();
    atoExpected.setTotalCost(new BigDecimal("2629.15"));
    atoExpected.setPositionQty(new BigDecimal("0.20"));
    atoExpected.setMsId(1);
    atoExpected.setSymbol(IdCoin.DUMMY.name());
    atoExpected.setPeriod(MIN1);
    atoExpected.setProductType(SPOT);
    atoExpected.setStatus(PM);
    atoExpected.setStopLogicOperator(AND);
    atoExpected.setFirstTrade(new Trade());
    atoExpected.getFirstTrade().setPrice(new BigDecimal("13145.740000000000000000"));
    atoExpected.getFirstTrade().setQty(new BigDecimal("0.2000"));
    atoExpected.getFirstTrade().setLongShort(LONG);
    atoExpected.getFirstTrade().setOrderType(MARKET);
    atoExpected.setLastTrade(new Trade());
    atoExpected.getLastTrade().setPrice(new BigDecimal("13145.740000000000000000"));
    atoExpected.getLastTrade().setQty(new BigDecimal("0.2000"));
    atoExpected.getLastTrade().setLongShort(LONG);
    atoExpected.getLastTrade().setOrderType(MARKET);
    return atoExpected;
  }


  private AutoTradeOrderSetup getAtoReboundMartingaleProfitReduceFromHighest(String symbol, Integer clientExchangeId) {

    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setProductType(SPOT);
    autoTradeOrderSetup.setClientExchangeId(clientExchangeId);
    autoTradeOrderSetup.setStatus(PM);

    ReboundMartingaleState reboundMartingaleState = new ReboundMartingaleState(
      new ReboundMartingaleSetup(5, false, new BigDecimal("0.50"), new BigDecimal("0.20")));
//    reboundMartingaleState.setLogicCode(EngineConstant.PmLogicCodes.ReboundMartingale.name());

    autoTradeOrderSetup.getPmStates().add(reboundMartingaleState);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.50"));
//    profitPercentageTpSetup.setLogicCode(EngineConstant.StopLogicCodes.ProfitPercentageTp.name());

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.20")));
//    profitReduceFromHighestState.setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestState);
    return autoTradeOrderSetup;
  }

}
