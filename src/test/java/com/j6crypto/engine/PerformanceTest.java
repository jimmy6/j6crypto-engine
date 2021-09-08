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
import com.j6crypto.to.setup.*;
import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static com.j6crypto.engine.entity.ClientExchange.Exchange.BINANCE;
import static com.j6crypto.engine.entity.MasterDataKey.IdCoin.BNBBUSD;
import static com.j6crypto.to.Trade.LongShort.LONG;
import static com.j6crypto.to.Trade.LongShort.SHORT;
import static com.j6crypto.to.Trade.OrderType.MARKET;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator.A;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.LogicOperator.AND;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Period.MIN1;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.ProductType.SPOT;
import static com.j6crypto.to.setup.AutoTradeOrderSetup.Status.*;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceTest extends ApiTestBase {
  private static Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

  public static String atoId;
  private static ClientExchangeTo clientExchangeToBinance, clientExchangeToDummy;
  private static AutoTradeOrderSetup autoTradeOrderSetup;
  private static Client client = new Client();

  @Test
  public void test1PostClient() {
    client.setName("j6crypto");
    client.setEmail(System.nanoTime() + "@gmail.com");
    client.setPassword(client.getEmail());
    ResponseEntity responseEntity = restTemplate.postForEntity(getClientApiPath(), client, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test(expected = HttpClientErrorException.class)
  public void test2AuthLoginWrongPassword() {
    LoginReq loginReq = new LoginReq();
    loginReq.setUsername(client.getEmail());
    loginReq.setPassword(client.getPassword() + "MakeMeWrong");

    try {
      restTemplate.postForEntity(getAuthApiPath("login"), loginReq, Void.class);
    } catch (HttpClientErrorException ex) {
      Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
      Assertions.assertTrue(ex.getResponseBodyAsString().contains("Wrong username or password."));
      throw ex;
    }

  }

  @Test
  public void test2AuthLogin() {
    LoginReq loginReq = new LoginReq();
    loginReq.setUsername(client.getEmail());
    loginReq.setPassword(client.getPassword());

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

    clientExchangeToBinance = getClientExchange(clientExchangeTos.getBody(), BINANCE);
    clientExchangeToDummy = getClientExchange(clientExchangeTos.getBody(), ClientExchange.Exchange.DUMMY);
  }

  @NotNull
  private ClientExchangeTo getClientExchange(ClientExchangeTo[] clientExchangeTos, ClientExchange.Exchange exchange) {
    return Arrays.stream(clientExchangeTos).filter(ex -> exchange.equals(ex.getExchange())).findAny().get();
  }

  @Test
  public void test7DummyReboundMartingaleProfitReduceFromHighest() {
    Integer clientExchangeId = clientExchangeToDummy.getId();
    AutoTradeOrderSetup atoSetup = getAtoReboundMartingaleProfitReduceFromHighest(IdCoin.DUMMY.name(), clientExchangeId);
    IntStream.range(1, 1000).forEach(i -> {
        Integer atoId = postAto(atoSetup);
      }
    );

    sendPrice("13145.74");//set starting coin price
    AutoTradeOrder atoExpected = getAtoExpected();

    sendPriceAfterPercentage("90");

    sendPriceAfterPercentage("100.51");
    atoExpected.getLastTrade().setPrice(new BigDecimal("11891.504946600000000000"));
    atoExpected.setTotalCost(new BigDecimal("5007.45"));
    atoExpected.setPositionQty(new BigDecimal("0.40"));

    //logger.info("Count totalCost's cost price ", atoExpected.getTotalCost().divide(atoExpected.getPositionQty()).toPlainString());
    sendPrice("12518.625");//set coin price to totalCost's cost price
    sendPriceAfterPercentage("100.71");
    sendPriceAfterPercentage("99.79");

  }

  private void sendPriceTriggerStopBreakSupportCrossValueTrigger(Integer atoId) {
    IntStream.range(1, 10).forEach(i -> sendPrice("13145.74"));//set starting coin price
    sendPrice("13145.74");
    sendPrice("13146.66");//resistance
    sendPriceAfterPercentage("95");
    sendPrice("11000.00");//support
    sendPrice("11001.00");
    sendPrice("11002.00");
    sendPrice("11004.00");
    sendPrice("13146.65");//below resistance 0.01. Drawback! - if this is 13146.66(resistance) then it will not trigger CrossValue
    sendPrice("13146.67");//1st break
    sendPrice("13146.99");//1 cachePeriod
    sendPrice("13144.01");//back below resistance. 2 cachePeriod
    sendPrice("13147.66");//2d break resistance/cross value. 3 cachePeriod
    sendPrice("13143.01");//back below resistance. 4 cachePeriod
    sendPrice("13143.01");//5 cachePeriod
    sendPrice("13142.01");//6 cachePeriod
    Assertions.assertNull(getAto(atoId).getFirstTrade());
    sendPrice("13146.68");//3rd break resistance/cross value. 7 cachePeriod
  }

  @NotNull
  private AutoTradeOrder getBreakSupportResistanceCrossValueTriggerExpected2() {
    AutoTradeOrder atoExpected = getAtoDummyBreakSupportResistanceExpected();
    atoExpected.setStatus(STOP);
    atoExpected.getFirstTrade().setPrice(new BigDecimal("13146.680000000000000000"));
    atoExpected.getFirstTrade().setQty(new BigDecimal("0.2000"));
    atoExpected.getLastTrade().setPrice(new BigDecimal("13146.680000000000000000"));
    atoExpected.getLastTrade().setQty(new BigDecimal("0.2000"));
    atoExpected.setTotalCost(new BigDecimal("2629.34"));
    atoExpected.setPositionQty(new BigDecimal("0.20"));
    atoExpected.setTriggerLogicOperator(A);
    return atoExpected;
  }

  @NotNull
  private AutoTradeOrder getBreakSupportResistanceCrossValueTriggerExpected() {
    AutoTradeOrder atoExpected = getAtoDummyBreakSupportResistanceExpected();
    atoExpected.setStatus(STOP);
    atoExpected.getFirstTrade().setPrice(new BigDecimal("13146.680000000000000000"));
    atoExpected.getLastTrade().setPrice(new BigDecimal("13146.680000000000000000"));
    atoExpected.setTotalCost(new BigDecimal("13146.68"));
    atoExpected.setTriggerLogicOperator(A);
    return atoExpected;
  }

  private AutoTradeOrder getAtoDummyBreakSupportResistanceExpected() {
    AutoTradeOrder atoExpected = new AutoTradeOrder();
    atoExpected.setTotalCost(new BigDecimal("13146.67"));
    atoExpected.setPositionQty(new BigDecimal("1"));
    atoExpected.setMsId(1);
    atoExpected.setSymbol(IdCoin.DUMMY.name());
    atoExpected.setPeriod(MIN1);
    atoExpected.setProductType(SPOT);
    atoExpected.setStatus(TRIGGER);
    atoExpected.setStopLogicOperator(AND);
    atoExpected.setFirstTrade(new Trade());
    atoExpected.getFirstTrade().setPrice(new BigDecimal("13146.670000000000000000"));
    atoExpected.getFirstTrade().setQty(new BigDecimal("1.0000"));
    atoExpected.getFirstTrade().setLongShort(LONG);
    atoExpected.getFirstTrade().setOrderType(MARKET);
    atoExpected.setLastTrade(new Trade());
    atoExpected.getLastTrade().setPrice(new BigDecimal("13146.670000000000000000"));
    atoExpected.getLastTrade().setQty(new BigDecimal("1.0000"));
    atoExpected.getLastTrade().setLongShort(LONG);
    atoExpected.getLastTrade().setOrderType(MARKET);
    return atoExpected;
  }

  private AutoTradeOrderSetup getAtoBreakSupportResistanceCrossValueTrigger(String symbol, Integer clientExchangeId) {
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setProductType(SPOT);
    autoTradeOrderSetup.setClientExchangeId(clientExchangeId);
    autoTradeOrderSetup.setStatus(TRIGGER);
    autoTradeOrderSetup.setTriggerLogicOperator(AutoTradeOrderSetup.LogicOperator.A);

    BreakSupportResistanceTriggerSetup triggerSetup = new BreakSupportResistanceTriggerSetup(7);
    triggerSetup.setCacheSignalForPeriod(7);
    autoTradeOrderSetup.getTriggerStates().add(triggerSetup);

    CrossValueTriggerSetup crossPreviousValueSetup = new CrossValueTriggerSetup(3);
    crossPreviousValueSetup.setValueFrom(EngineConstant.TriggerLogicCodes.BreakSupportResistanceTrigger.name());
    autoTradeOrderSetup.getTriggerStates().add(crossPreviousValueSetup);

    MartingaleDoublePmSetup martingaleDoublePmSetup = new MartingaleDoublePmSetup(0, false, BigDecimal.ONE);
    autoTradeOrderSetup.setPmState(martingaleDoublePmSetup);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));

    ProfitReduceFromHighestSetup profitReduceFromHighestSetup = new ProfitReduceFromHighestSetup(new BigDecimal("0.2"));

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestSetup);
    return autoTradeOrderSetup;
  }

  private AutoTradeOrderSetup getAtoBreakSupportResistanceCrossValueTriggerReboundTriggerMartingaleDoublePm(String symbol, Integer clientExchangeId) {
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setProductType(SPOT);
    autoTradeOrderSetup.setClientExchangeId(clientExchangeId);
    autoTradeOrderSetup.setStatus(TRIGGER);
    autoTradeOrderSetup.setTriggerLogicOperator(AutoTradeOrderSetup.LogicOperator.A);
    BreakSupportResistanceTriggerSetup triggerSetup = new BreakSupportResistanceTriggerSetup(7);
    triggerSetup.setCacheSignalForPeriod(7);
    autoTradeOrderSetup.getTriggerStates().add(triggerSetup);

    CrossValueTriggerSetup crossPreviousValueSetup = new CrossValueTriggerSetup(3);
    crossPreviousValueSetup.setValueFrom(EngineConstant.TriggerLogicCodes.BreakSupportResistanceTrigger.name());
    autoTradeOrderSetup.getTriggerStates().add(crossPreviousValueSetup);

    ReboundSetup reboundSetup = new ReboundSetup();
    reboundSetup.setReboundEnterPerc(new BigDecimal("0.50"));
    autoTradeOrderSetup.getPmStates().add(reboundSetup);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));
    ProfitReduceFromHighestSetup profitReduceFromHighestSetup = new ProfitReduceFromHighestSetup(new BigDecimal("0.2"));

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestSetup);

    MartingaleDoublePmSetup martingaleDoublePmSetup = new MartingaleDoublePmSetup();
    autoTradeOrderSetup.setPmState(martingaleDoublePmSetup);
    martingaleDoublePmSetup.setNoOfMartingale(2);
    martingaleDoublePmSetup.setTradeSizeDoubleMartingale(true);
    martingaleDoublePmSetup.setTradeQty(new BigDecimal("0.20"));

    return autoTradeOrderSetup;
  }

  private AutoTradeOrderSetup getAtoBreakSupportResistanceTrigger(String symbol, Integer clientExchangeId) {
    AutoTradeOrderSetup autoTradeOrderSetup = new AutoTradeOrderSetup();
    autoTradeOrderSetup.setSymbol(symbol);
    autoTradeOrderSetup.setProductType(SPOT);
    autoTradeOrderSetup.setClientExchangeId(clientExchangeId);
    autoTradeOrderSetup.setStatus(TRIGGER);

    BreakSupportResistanceTriggerSetup triggerSetup = new BreakSupportResistanceTriggerSetup(6);
    autoTradeOrderSetup.getTriggerStates().add(triggerSetup);

    MartingaleDoublePmSetup martingaleDoublePmSetup = new MartingaleDoublePmSetup(0, false, BigDecimal.ONE);
    autoTradeOrderSetup.setPmState(martingaleDoublePmSetup);

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.5"));
    ProfitReduceFromHighestSetup profitReduceFromHighestSetup = new ProfitReduceFromHighestSetup(new BigDecimal("0.2"));

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestSetup);
    return autoTradeOrderSetup;
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
    autoTradeOrderSetup.setStatus(TRIGGER);

    DummyTriggerSetup dummyTriggerSetup = new DummyTriggerSetup(LONG);
    autoTradeOrderSetup.getTriggerStates().add(dummyTriggerSetup);

    ReboundSetup reboundSetup = new ReboundSetup(new BigDecimal("0.50"));
    autoTradeOrderSetup.getPmStates().add(reboundSetup);

    MartingaleDoublePmSetup martingaleDoublePmSetup = new MartingaleDoublePmSetup();
    autoTradeOrderSetup.setPmState(martingaleDoublePmSetup);
    martingaleDoublePmSetup.setNoOfMartingale(5);
    martingaleDoublePmSetup.setTradeSizeDoubleMartingale(false);
    martingaleDoublePmSetup.setTradeQty(new BigDecimal("0.20"));

    ProfitPercentageTpSetup profitPercentageTpSetup = new ProfitPercentageTpSetup(new BigDecimal("0.50"));

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.20")));

    autoTradeOrderSetup.getStopStates().add(profitPercentageTpSetup);
    autoTradeOrderSetup.getStopStates().add(profitReduceFromHighestState);
    return autoTradeOrderSetup;
  }

}
