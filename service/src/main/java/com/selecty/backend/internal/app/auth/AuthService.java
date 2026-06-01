package com.selecty.backend.internal.app.auth;

import com.selecty.backend.internal.ports.UserRepository;

/**
 * Application use-cases for auth. No Spring/framework imports.
 */
public class AuthService {
  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void register(String username, String password) {
    // TODO: hash password and persist via UserRepository
    userRepository.save(username, password);
  }

  public boolean credentialsValid(String username, String password) {
    return userRepository.findUsernameByCredentials(username, password).isPresent();
  }
}
