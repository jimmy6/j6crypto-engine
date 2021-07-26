package com.j6crypto;

import com.j6crypto.engine.CoinDataProducer;
import com.j6crypto.engine.CryptoEngine;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceInstance;
import org.springframework.cloud.zookeeper.discovery.reactive.ZookeeperReactiveDiscoveryClient;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.j6crypto",
  excludeFilters = {@ComponentScan.Filter(SpringBootApplication.class),
    @ComponentScan.Filter(type = ASSIGNABLE_TYPE, value = CoinDataProducer.class)})
public class J6CryptoEngineApp implements CommandLineRunner {
  @Autowired
  private CryptoEngine cryptoEngine;
  @Autowired
  private ZookeeperDiscoveryClient discoveryClient;

  //  @Autowired
  private CuratorZookeeperClient zooKeeperConnection;
  @Autowired
  private ZookeeperServiceRegistry serviceRegistry;
  ZookeeperReactiveDiscoveryClient zookeeperReactiveDiscoveryClient;
  @Autowired
  private ServiceDiscovery serviceDiscovery;

  public static void main(String[] args) {
//    SpringApplication springApplication = new SpringApplication(J6CryptoEngineApp.class);
    ConfigurableApplicationContext context = SpringApplication.run(J6CryptoEngineApp.class, args);

  }

  /**
   * On start up ms
   * 1 - look for DB running status, msId which no instance in zookeeper. else ignore
   * -  submit InstanceId - msId/dbId
   * 2 - submit memory
   * On lookup msId in request
   * 1 - lookup db
   *
   * @param args
   */
  @Override
  public void run(String... args) {
    try {
//      cryptoEngine.init();
      discoveryClient.getServices().stream().forEach(s -> {
        System.out.println("service = " + s);
      });
//      zooKeeperConnection.getZooKeeper().setACL()
//      zookeeperReactiveDiscoveryClient.getInstances().


      System.out.println("service1 = " + discoveryClient.getOrder());
      int c = 0;
      while (true) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("j6crypto-engine");

        for (int i = 0; i < serviceInstances.size(); i++) {
          System.out.println("size = " + serviceInstances.size());
          System.out.println("service c = " + serviceInstances.get(i).getPort());
          System.out.println("service2 = " + serviceInstances.get(i).getMetadata());
          System.out.println("service3 = " + serviceInstances.get(i).getInstanceId());
//          ((ZookeeperServiceInstance) serviceInstances.get(0)).getServiceInstance().getPayload().getMetadata().put("engine-capacity", c++ + "");
//          serviceDiscovery.updateService(((ZookeeperServiceInstance) serviceInstances.get(0)).getServiceInstance());
          Thread.sleep(1000);
//          serviceRegistry.setStatus(new ZookeeperRegistration(){
//            @Override
//            public String getServiceId() {
//              return null;
//            }
//
//            @Override
//            public String getHost() {
//              return null;
//            }
//
//            @Override
//            public int getPort() {
//              return 0;
//            }
//
//            @Override
//            public boolean isSecure() {
//              return false;
//            }
//
//            @Override
//            public URI getUri() {
//              return null;
//            }
//
//            @Override
//            public Map<String, String> getMetadata() {
//              return null;
//            }
//
//            @Override
//            public org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstance> getServiceInstance() {
//              return ((ZookeeperServiceInstance)serviceInstances.get(0)).getServiceInstance();
//            }
//
//            @Override
//            public void setPort(int port) {
//
//            }
//          }, "na");

          Thread.sleep(1000);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}