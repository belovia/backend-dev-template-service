package com.selecty.backend.internal.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Helpers for session cookie auth (Spring Security + HttpSession).
 */
public final class SessionAuthSupport {
  private SessionAuthSupport() {}

  public static void bindAuthenticationToSession(Authentication authentication, HttpServletRequest request) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    request.getSession(true).setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        context
    );
  }
}
