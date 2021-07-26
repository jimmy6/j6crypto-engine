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
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class ApiTestBase {
  public static final String API_KEY = "zAkuceYTazpLli0ZLdfNMexHys8wZcIY3RQKZZPRpA9l3AgZCWFJq885Aj38iAfP";
  public static final String SECRET_KEY = "bYZT1aDb1n1gFIN4pe4hsYDVYkpAXiw5qNuCYBzEyzNHW5bvykJp1ONtohbTrvbb";
  //  public static final String URL = "http://127.0.0.1:8085/";
  public static final String URL = "http://127.0.0.1:8080/";
//  public static final String URL = "http://8.210.145.139:8082/";


  protected static String authToken;

  public static BigDecimal price = new BigDecimal("123.122335");
  public static String dummyCoin = "DUMMY";
  public static LocalDateTime dummyDateTime = LocalDateTime.now().withSecond(59);
  public static RestTemplate restTemplate = new RestTemplateBuilder()
    .messageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
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
    headers.add("Authorization", authToken);
    return headers;
  }

  public Integer postAto(AutoTradeOrderSetup atoSetup) {
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
  }

  public static void sendPriceAfterPercentage(String percentage) {
    price = EngineUtil.priceAfterPercentage(price, new BigDecimal(percentage));
    sendMonitor(dummyCoin, price);
  }

  private void assertAto(AutoTradeOrder totalCost, String positionQty, String s1, String s2, String s3, Trade.LongShort aLong) {
//    Assertions.assertEquals();
  }

  public String[] ignoreAtoFields() {
    return new String[]{"clientExchangeId", "clientId", "id", "firstTrade", "lastTrade", "triggerStates", "pmStates", "stopStates", "msId"};
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
