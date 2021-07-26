package com.j6crypto.service;

import com.j6crypto.controller.to.ClientExchangeTo;
import com.j6crypto.engine.entity.Client;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.repo.ClientExchangeRepo;
import com.j6crypto.repo.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Service
public class ClientService {
  @Autowired
  private SecurityService securityService;
  @PersistenceContext
  private EntityManager em;
  @Autowired
  private ClientRepo clientRepo;
  @Autowired
  private ClientExchangeRepo clientExchangeRepo;

  @Transactional
  public void createClient(Client client) {
    clientRepo.save(client);
    securityService.setClientId(client.getId());
  }

  @Transactional
  public void createClientExchange(ClientExchangeTo clientExchangeTo) {
    ClientExchange clientExchange = new ClientExchange();
    clientExchange.setClient(new Client(securityService.getClientId()));
    clientExchange.setApiKey(clientExchangeTo.getApiKey());
    clientExchange.setSecretKey(clientExchangeTo.getSecretKey());
    clientExchange.setExchange(clientExchangeTo.getExchange());

    em.persist(clientExchange);
  }

  @Transactional(readOnly = true)
  public List<ClientExchangeTo> getClientExchanges() {
    List<ClientExchange> clientExchanges = clientExchangeRepo.findByClientId(securityService.getClientId());
    return clientExchanges.stream().map(this::transform).collect(Collectors.toList());
  }

  private ClientExchangeTo transform(ClientExchange clientExchange) {
    ClientExchangeTo clientExchangeTo = new ClientExchangeTo();
    clientExchangeTo.setId(clientExchange.getId());
    clientExchangeTo.setExchange(clientExchange.getExchange());
    clientExchangeTo.setApiKey(clientExchange.getApiKey());
    clientExchangeTo.setSecretKey(clientExchange.getSecretKey());
    return clientExchangeTo;
  }
}
