package com.selecty.backend.internal.ports;

import java.util.Optional;

/**
 * Port for user persistence. Implementations live in {@code internal.adapters.repo}.
 */
public interface UserRepository {
  Optional<String> findUsernameByCredentials(String username, String password);

  void save(String username, String passwordHash);
}
