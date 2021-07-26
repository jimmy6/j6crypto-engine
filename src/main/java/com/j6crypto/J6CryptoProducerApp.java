package com.j6crypto;

import com.j6crypto.engine.CoinDataProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.j6crypto",
  excludeFilters = @ComponentScan.Filter(SpringBootApplication.class))
public class J6CryptoProducerApp implements ApplicationRunner {
  @Autowired
  private CoinDataProducer coinDataProducer;

  public static void main(String[] args) {
    SpringApplication.run(J6CryptoProducerApp.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      coinDataProducer.pullData();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}