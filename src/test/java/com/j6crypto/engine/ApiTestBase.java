package com.j6crypto.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.TimeData;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ApiTestBase {
  public static final String API_KEY = "asdsadsadas";
  public static final String SECRET_KEY = "sadsadsadasdsa";
  public static final String URL = "http://127.0.0.1:8080/";//gateway
//  public static final String URL = "http://test5.cn-hongkong.alicontainer.com/";
//public static final String URL = "http://127.0.0.1:8081/";//direct engine

  protected static String authToken = "999";

  public static BigDecimal price = new BigDecimal("123.122335");
  public static String dummyCoin = "DUMMY";
  public static LocalDateTime dummyDateTime = LocalDateTime.now().withSecond(59);
  public static RestTemplate restTemplate = new RestTemplateBuilder()
    .messageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
    .setConnectTimeout(Duration.ofMinutes(10))
    .setReadTimeout(Duration.ofMinutes(10))
    .build();

  protected static <T> HttpEntity<T> entity() {
    return entity(null);
  }

  protected static <T> HttpEntity<T> entity(T body) {
    HttpHeaders headers = getHeader();
    return new HttpEntity(body, headers);
  }

  protected static HttpHeaders getHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", authToken);
    return headers;
  }

  public Integer postAto(AutoTradeOrderSetup atoSetup) {
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    return restTemplate.postForObject(getEngineApiPath("ato"), entity(atoSetup), Integer.class);
  }

  public void assertAll(AutoTradeOrder ato, AutoTradeOrder atoExpected) {
    assertThat(ato.getFirstTrade(), Matchers.samePropertyValuesAs(atoExpected.getFirstTrade()));
    assertThat(ato.getLastTrade(), Matchers.samePropertyValuesAs(atoExpected.getLastTrade()));
    assertNotNull(ato.getMsId());

    assertThat(ato, Matchers.samePropertyValuesAs(atoExpected, ignoreAtoFields()));
  }

  public static void sendMonitor(BigDecimal price) {
    sendMonitor(dummyCoin, price);
  }

  public static void sendPrice(String price) {
    sendMonitor(dummyCoin, new BigDecimal(price));
  }

  public static void sendMonitor(String code, BigDecimal price) {
    ApiTestBase.price = price;
    dummyDateTime = dummyDateTime.plusMinutes(1);
    restTemplate.postForObject(getEngineApiPath("monitor"), entity(new TimeData(code, price, dummyDateTime)), Void.class);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void sendPriceAfterPercentage(String percentage) {
    price = EngineUtil.priceAfterPercentage(price, new BigDecimal(percentage));
    sendMonitor(dummyCoin, price);
  }

  public String[] ignoreAtoFields() {
    return new String[]{"clientExchangeId", "clientId", "id", "firstTrade", "lastTrade", "triggerStates", "pmStates", "pmState", "stopStates", "msId"};
  }

  public static AutoTradeOrder getAto(Integer atoId) {
    return restTemplate.exchange(getEngineApiPath("ato/" + atoId), GET, entity(), AutoTradeOrder.class).getBody();
  }

  public static String getEngineApiPath(String path) {
    return URL + "engine/" + path;
  }

  public static String getClientApiPath(String... path) {
    return URL + "client" + ((path.length == 0) ? "" : "/" + path[0]);
  }

  public static String getAuthApiPath(String... path) {
    return URL + "auth" + ((path.length == 0) ? "" : "/" + path[0]);
  }
}
