package com.j6crypto.repo;

import com.j6crypto.engine.entity.ClientExchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public interface ClientExchangeRepo extends JpaRepository<ClientExchange, Integer> {
  List<ClientExchange> findByClientId(int clientId);
}
