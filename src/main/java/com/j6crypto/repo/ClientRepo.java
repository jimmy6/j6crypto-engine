package com.j6crypto.repo;

import com.j6crypto.engine.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public interface ClientRepo extends JpaRepository<Client, Integer> {
  Optional<Client> findByEmailAndPassword(String username, String password);
}
