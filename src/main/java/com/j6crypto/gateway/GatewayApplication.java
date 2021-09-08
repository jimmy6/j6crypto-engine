package com.j6crypto.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SpringBootApplication()
@Component
public class GatewayApplication {
  private static Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

  @Value("${spring.cloud.zookeeper.discovery.instance-ip-address}")
  private String ipAddress;

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

  @PostConstruct
  public void getSt() {
    logger.warn("ipAddress=" + ipAddress);
//    InetUtils inetUtils = new InetUtils();

  }

}
	