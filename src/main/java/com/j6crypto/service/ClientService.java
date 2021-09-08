package com.j6crypto.service;

import com.j6crypto.controller.to.ClientExchangeTo;
import com.j6crypto.engine.entity.Client;
import com.j6crypto.engine.entity.ClientExchange;
import com.j6crypto.repo.ClientExchangeRepo;
import com.j6crypto.repo.ClientRepo;
import com.j6crypto.to.LoginReq;
import com.j6crypto.to.LoginRes;
import com.j6crypto.web.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Service
public class ClientService {
  @Autowired
  private ClientContext clientContext;
  @PersistenceContext
  private EntityManager em;
  @Autowired
  private ClientRepo clientRepo;
  @Autowired
  private ClientExchangeRepo clientExchangeRepo;
  @Autowired
  private JwtService jwtService;

  @Transactional
  public void createClient(Client client) {
    clientRepo.save(client);
    clientContext.setClientId(client.getId());//TODO set here correct? no login?
  }

  @Transactional
  public void createClientExchange(ClientExchangeTo clientExchangeTo) {
    ClientExchange clientExchange = new ClientExchange();
    clientExchange.setClient(new Client(clientContext.getClientId()));
    clientExchange.setApiKey(clientExchangeTo.getApiKey());
    clientExchange.setSecretKey(clientExchangeTo.getSecretKey());
    clientExchange.setExchange(clientExchangeTo.getExchange());

    em.persist(clientExchange);
  }

  @Transactional(readOnly = true)
  public List<ClientExchangeTo> getClientExchanges() {
    List<ClientExchange> clientExchanges = clientExchangeRepo.findByClientId(clientContext.getClientId());
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

  public LoginRes login(LoginReq loginReq) {
    Client client = clientRepo.findByEmailAndPassword(loginReq.getUsername(), loginReq.getPassword())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong username or password."));
    String token = jwtService.generateToken(client.getId() + "");
    return new LoginRes(token);
  }
}
