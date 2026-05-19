package com.selecty.backend.internal.adapters.repo;

import com.selecty.backend.internal.ports.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Stub repository until JPA/users table is added.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
  @Override
  public Optional<String> findUsernameByCredentials(String username, String password) {
    if ("demo".equals(username) && "demo".equals(password)) {
      return Optional.of(username);
    }
    return Optional.empty();
  }

  @Override
  public void save(String username, String passwordHash) {
    // no-op stub
  }
}
