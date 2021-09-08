package com.j6crypto.gateway.config;

import com.j6crypto.gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class JwtAuthenticationFilter implements GatewayFilter {
  private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  final static List<String> apiPublicEndpoints = List.of("/client", "/auth/login");

  @Autowired
  private JwtService jwtService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();


    Predicate<ServerHttpRequest> isApiSecured = r -> apiPublicEndpoints.stream()
      .noneMatch(uri -> r.getURI().getPath().endsWith(uri));

    if (isApiSecured.test(request)) {
      if (!request.getHeaders().containsKey("Authorization")) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(UNAUTHORIZED);

        return response.setComplete();
      }

      final String token = request.getHeaders().getOrEmpty("Authorization").get(0);

      try {
        jwtService.validateToken(token);
      } catch (RuntimeException e) {
        logger.debug(e.getMessage());

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(BAD_REQUEST);

        return response.setComplete();
      }

      Claims claims = jwtService.getClaims(token);
      exchange.getRequest().mutate().header("clientId", String.valueOf(claims.getSubject())).build();
    }

    return chain.filter(exchange);
  }

}