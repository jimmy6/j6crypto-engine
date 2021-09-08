package com.j6crypto.config;

import com.j6crypto.repo.AutoTradeOrderRepo;
import com.j6crypto.service.EngineDiscoveryService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ConditionalOnZookeeperDiscoveryEnabled;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.serviceregistry.*;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration.RegistrationBuilder;
import org.springframework.cloud.zookeeper.support.StatusConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static com.j6crypto.service.EngineDiscoveryService.ENGINE_CAPACITY_NAME;


@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(type = "org.springframework.cloud.zookeeper.discovery.ZookeeperLifecycle")
@ConditionalOnZookeeperDiscoveryEnabled
//@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
@AutoConfigureAfter({ZookeeperServiceRegistryAutoConfiguration.class, AutoTradeOrderRepo.class, EngineDiscoveryService.class})
@AutoConfigureBefore({AutoServiceRegistrationAutoConfiguration.class, ZookeeperDiscoveryAutoConfiguration.class})
public class J6ZookeeperAutoServiceRegistrationAutoConfiguration {

  @Bean
  public ZookeeperAutoServiceRegistration zookeeperAutoServiceRegistration(
    ZookeeperServiceRegistry registry, ZookeeperRegistration registration,
    ZookeeperDiscoveryProperties properties, EngineDiscoveryService engineDiscoveryService) {
    properties.setInstanceId(engineDiscoveryService.getMsId() + "");
    return new ZookeeperAutoServiceRegistration(registry, registration, properties);
  }

  @Bean
  @ConditionalOnMissingBean(ZookeeperRegistration.class)
  public ServiceInstanceRegistration serviceInstanceRegistration(
    ApplicationContext context, ZookeeperDiscoveryProperties properties, EngineDiscoveryService engineDiscoveryService) {
    String appName = context.getEnvironment().getProperty("spring.application.name",
      "application");
    String host = properties.getInstanceHost();
    if (!StringUtils.hasText(host)) {
      throw new IllegalStateException("instanceHost must not be empty");
    }

    properties.getMetadata().put(StatusConstants.INSTANCE_STATUS_KEY, properties.getInitialStatus());
    properties.getMetadata().put(ENGINE_CAPACITY_NAME, "0;0;0");
    ZookeeperInstance zookeeperInstance = new ZookeeperInstance(engineDiscoveryService.getMsId() + "",
      appName, properties.getMetadata());
    RegistrationBuilder builder = ServiceInstanceRegistration.builder().address(host)
      .name(appName).payload(zookeeperInstance)
      .uriSpec(properties.getUriSpec());

    if (properties.getInstanceSslPort() != null) {
      builder.sslPort(properties.getInstanceSslPort());
    }
    if (properties.getInstanceId() != null) {
      builder.id(engineDiscoveryService.getMsId() + "");
    }

    return builder.build();
  }

}
