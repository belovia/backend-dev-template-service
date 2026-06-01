package com.selecty.backend.internal.config;

import com.selecty.backend.internal.app.auth.AuthService;
import com.selecty.backend.internal.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public AuthService authService(UserRepository userRepository) {
    return new AuthService(userRepository);
  }
}
