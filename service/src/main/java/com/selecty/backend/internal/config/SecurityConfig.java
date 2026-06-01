package com.selecty.backend.internal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // TODO(prod): replace CSRF ignore with token/header policy for cookie-based REST.
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers("/actuator/prometheus").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/hello").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .formLogin(form -> form.disable());

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    // TODO: replace with DB-backed users
    return new InMemoryUserDetailsManager(
        User.withUsername("demo")
            .password(passwordEncoder.encode("demo"))
            .roles("USER")
            .build()
    );
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
  }
}
