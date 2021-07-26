package com.j6crypto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j6crypto.engine.BinanceSpotTradingPlatform;
import com.j6crypto.engine.TradePlatform;
import com.j6crypto.to.TimeData;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Configuration
public class BeanConfig {

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplateBuilder()
      .messageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
      .build();
  }

  @Bean
  public Supplier<LocalDateTime> getCurrentDateTimeSupplier() {
    return new Supplier() {
      @Override
      public Object get() {
        return LocalDateTime.now();
      }
    };
  }

}
