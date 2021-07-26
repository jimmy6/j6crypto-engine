package com.j6crypto.engine;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.j6crypto.repo.MasterDataRepo;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.to.TimeData;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.j6crypto.engine.EngineConstant.COIN_TOPIC_PREFIX;
import static com.j6crypto.engine.EngineUtil.getEpochMinute;
import static com.j6crypto.engine.EngineUtil.toTimeData;
import static java.util.stream.Collectors.toList;


/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class CoinDataProducer {//TODO cluster producer
  private static Logger logger = LoggerFactory.getLogger(CoinDataProducer.class);
  @Autowired
  private MasterDataRepo masterDataRepo;
  private static Set<String> supportedSymbols = new HashSet<>();
  private static String supportedSymbolsStr = "";
  @Autowired
  private KafkaTemplate<String, TimeData> kafkaTemplate;
  @Autowired
  private KafkaAdmin kafkaAdmin;
  private BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
  private Closeable closeableClient;
  private TimeData timeData = new TimeData();

  @Autowired
  private AutoTradeOrderService atoService;

  public CoinDataProducer() {
    timeData.setDateTime(LocalDateTime.MIN);
  }

  public void pullData() {
    closeableClient = client.onCandlestickEvent(supportedSymbolsStr, CandlestickInterval.ONE_MINUTE,
      new BinanceApiCallback<CandlestickEvent>() {
        @Override
        public void onResponse(CandlestickEvent response) {
          if (response.getBarFinal()) {
            logger.debug(response.toString());
            timeData = toTimeData(response);
            kafkaTemplate.send(COIN_TOPIC_PREFIX + timeData.getCode(), timeData);
          }
        }
      });
  }

  @PostConstruct
  @Scheduled(fixedRate = 1000 * 60 * 10)//10 minute
  public void syncSupportedSymbol() {
    Set<String> dbSupportedSymbols = atoService.getSupportSymbol();
    if (!supportedSymbols.equals(dbSupportedSymbols)) {
      supportedSymbols = dbSupportedSymbols;
      supportedSymbolsStr = String.join(",", supportedSymbols).toLowerCase();
      try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
        adminClient.createTopics(
          supportedSymbols.stream().map(
            symbol -> TopicBuilder.name(COIN_TOPIC_PREFIX + symbol).partitions(1).replicas(2).build()
          ).collect(toList()));
      }
      refreshDataPulling();
    }
  }

  @Scheduled(cron = "10 * * * * *")
  public void monitorDataStream() {
    if (getEpochMinute(timeData.getDateTime()) + 1 != getEpochMinute(LocalDateTime.now())) {
      refreshDataPulling();
    }
  }

  private synchronized void refreshDataPulling() {
    try {
      if (closeableClient != null)
        closeableClient.close();
    } catch (Exception e) {
      logger.info(e.getMessage());
    }
    logger.info("Refresh BinanceApi Data Pull");
    pullData();
  }
}
