package com.j6crypto.engine;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Component
public class EngineClient {
  @Autowired
  private RestTemplate restTemplate;
  @Value("${server.port}")
  private String port;

  public void addAtoToCryptoEngine(int msId, int atoId) {
    restTemplate.getForObject(getEngineApiPath(msId, "ato/" + atoId + "/run"), Void.class);
  }

  //TODO configurable ip and serviceid
  private String getEngineApiPath(int msId, String path) {
    return "http://127.0.0.1:" + port + "/engine/" + path;
  }
}
