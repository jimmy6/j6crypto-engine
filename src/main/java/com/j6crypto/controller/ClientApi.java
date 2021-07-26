package com.j6crypto.controller;

import com.j6crypto.controller.to.ClientExchangeTo;
import com.j6crypto.engine.entity.Client;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@RestController
@RequestMapping("client")
public class ClientApi {
  @Autowired
  private ClientService clientService;

  @PostMapping
  public void createClient(@RequestBody Client client) {
    clientService.createClient(client);
  }

  @PostMapping(path = "/exchange")
  public void createExchange(@RequestBody ClientExchangeTo clientExchangeTo) {
    clientService.createClientExchange(clientExchangeTo);
  }

  @GetMapping(path = "/exchange")
  public List<ClientExchangeTo> getClientExchanges() {
    return clientService.getClientExchanges();
  }
}
