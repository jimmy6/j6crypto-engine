package com.j6crypto.service;

import com.j6crypto.J6CryptoEngineLocalTestApp;
import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.repo.AutoTradeOrderRepo;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceInstance;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class EngineDiscoveryService {
  private static Logger logger = LoggerFactory.getLogger(EngineDiscoveryService.class);
  public static final String ENGINE_SERVICE_NAME = "j6crypto-engine";
  public static final String ENGINE_CAPACITY_NAME = "engine-capacity";
  private static final String CAPACITY_DELIMITER = ";";

  @Autowired
  private ZookeeperDiscoveryClient discoveryClient;
  @Autowired
  private AutoTradeOrderRepo atoRepo;
  private int msId;
  @Autowired
  private ServiceDiscovery serviceDiscovery;

  public int getMsId() {
    return msId;
  }

  @PostConstruct
  public Integer init() {
    Set<Integer> activeAtoMsIds = atoRepo.findDistinctMsIdByStatusIn(CryptoEngine.ACTIVE_ATO_STATUS);
    Set<Integer> runningMsIds = discoveryClient.getInstances(ENGINE_SERVICE_NAME).stream().map(
      serviceInstance -> Integer.parseInt(serviceInstance.getInstanceId())).collect(Collectors.toSet());

    Set<Integer> unassignedMsIds = new HashSet<>(activeAtoMsIds);
    unassignedMsIds.remove(runningMsIds);

    Integer msIdToRegister = Stream.concat(activeAtoMsIds.stream(), runningMsIds.stream()).mapToInt(v -> v).max().orElseGet(() -> 0) + 1;
    //ServiceDiscoveryImpl. 175 this.client.delete().forPath(path);
    //TODO threadsafe / duplicated msId will cause delete existing msId in zookeeper, so the existing ms will still be running

    if (!unassignedMsIds.isEmpty()) {
      Integer msId = unassignedMsIds.iterator().next();
      msIdToRegister = msId;
      unassignedMsIds.remove(msId);
    }
    if (unassignedMsIds.size() >= 1) {
      logger.warn("MS Id not assign {}" + unassignedMsIds);//TODO what if still has unassign
    }

    msId = msIdToRegister;
    return msIdToRegister;
  }

  @Scheduled(cron = "0/4 * * * * *")
  public void updateEngineCapacity() throws Exception {
    ServiceInstanceRegistration registration = getServiceInstanceRegistration();
    registration.getMetadata().put(ENGINE_CAPACITY_NAME, getCapacityStr());
    serviceDiscovery.updateService(registration.getServiceInstance());
  }

  public String getCapacityStr() {
    return Runtime.getRuntime().totalMemory() + CAPACITY_DELIMITER
      + Runtime.getRuntime().freeMemory() + CAPACITY_DELIMITER
      + getCryptoEngine().getAtoCount();
  }

  @Lookup
  public ServiceInstanceRegistration getServiceInstanceRegistration() {
    return null;
  }

  @Lookup
  public CryptoEngine getCryptoEngine() {
    return null;
  }

  public void terminateBot(int msId) {
//    discoveryClient.getInstances(ENGINE_SERVICE_NAME).forEach();
  }

  public void getEngineInstanceByMsid(int msId) {
//    discoveryClient.getInstances(ENGINE_SERVICE_NAME).stream().filter(instance ->{
//      ((ZookeeperServiceInstance) instance).getServiceInstance().getPayload().getMetadata().get()
//    }).findFirst()
  }

}
