package com.selecty.backend.internal.adapters.http.auth;

import com.selecty.backend.internal.app.auth.AuthService;
import com.selecty.backend.internal.auth.SessionAuthSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final AuthService authService;

  public AuthController(AuthenticationManager authenticationManager, AuthService authService) {
    this.authenticationManager = authenticationManager;
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<StatusResponse> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request.username(), request.password());
    return ResponseEntity.ok(new StatusResponse("TODO"));
  }

  @PostMapping("/login")
  public ResponseEntity<StatusResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest
  ) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    SessionAuthSupport.bindAuthenticationToSession(authentication, httpRequest);
    return ResponseEntity.ok(new StatusResponse("OK"));
  }

  @PostMapping("/logout")
  public ResponseEntity<StatusResponse> logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    new SecurityContextLogoutHandler().logout(request, response, auth);
    return ResponseEntity.ok(new StatusResponse("OK"));
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> me() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(new MeResponse(auth.getName()));
  }

  public record RegisterRequest(@NotBlank String username, @NotBlank String password) {}

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

  public record StatusResponse(String status) {}

  public record MeResponse(String username) {}
}
