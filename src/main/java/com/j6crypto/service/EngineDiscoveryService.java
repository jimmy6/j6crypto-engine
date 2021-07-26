package com.j6crypto.service;

import com.j6crypto.J6CryptoEngineLocalTestApp;
import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.repo.AutoTradeOrderRepo;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceInstance;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  @Autowired
  private ZookeeperDiscoveryClient discoveryClient;
  @Autowired
  private AutoTradeOrderRepo atoRepo;
  private int msId;

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
      logger.warn("MS Id not assign {}" + unassignedMsIds);
    }

//    registrationBuilder.id(msIdToRegister + "");
//    registrationBuilder.name(ENGINE_SERVICE_NAME);
//    ServiceInstanceRegistration serviceInstanceRegistration = registrationBuilder.payload(
//      new ZookeeperInstance(msIdToRegister + "", ENGINE_SERVICE_NAME, new HashMap<>())).build();
//    serviceInstanceRegistration.getServiceInstance();
//    serviceInstanceRegistration.getMetadata().put(ENGINE_CAPACITY_NAME, "0");//TODO set 0
//    serviceDiscovery.updateService(serviceInstanceRegistration.getServiceInstance());
    msId = msIdToRegister;
    return msIdToRegister;
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
