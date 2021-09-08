package com.j6crypto.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Configuration
public class GatewayConfig {
  @Autowired
  private JwtAuthenticationFilter filter;

  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes().route("j6crypto-engine",
      r -> r.path("/engine/**", "/client/**", "/auth/**")
        .filters(f -> f.filter(filter)).uri("lb://j6crypto-engine"))
//      .route("alert", r -> r.path("/alert/**").filters(f -> f.filter(filter)).uri("lb://alert"))
      .build();
  }
}
