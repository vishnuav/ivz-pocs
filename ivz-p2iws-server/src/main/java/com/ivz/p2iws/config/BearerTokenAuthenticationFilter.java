package com.ivz.p2iws.config;

import com.ivz.jwt.security.JwtTokenService;
import com.ivz.jwt.security.JwtTokenValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenService jwtTokenService;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  public BearerTokenAuthenticationFilter(JwtTokenService jwtTokenService, AuthenticationEntryPoint authenticationEntryPoint) {
    this.jwtTokenService = jwtTokenService;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    if (!requiresAuthentication(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      authenticationEntryPoint.commence(request, response,
        new InsufficientAuthenticationException("Missing Bearer token"));
      return;
    }

    String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    JwtTokenValidationResult validationResult = jwtTokenService.validateToken(token);
    if (!validationResult.isValid()) {
      authenticationEntryPoint.commence(request, response,
        new InsufficientAuthenticationException(validationResult.getFailureMessage()));
      return;
    }

    Authentication authentication = new UsernamePasswordAuthenticationToken(validationResult.getSubject(), token, List.of());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilterErrorDispatch() {
    return false;
  }

  @Override
  protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    doFilterInternal(request, response, filterChain);
  }

  private boolean requiresAuthentication(HttpServletRequest request) {
    return "/api/orders/status".equals(request.getServletPath());
  }
}