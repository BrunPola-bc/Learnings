package com.brunpola.cv_management.security.filters;

import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.services.JwtService;
import com.brunpola.cv_management.security.services.SecurityUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that validates JWT tokens in incoming requests and sets the authentication context.
 *
 * <p>This filter is executed once per request and ensures that requests carrying a valid JWT
 * authenticate the corresponding {@link SecurityUser}.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /** Service for validating and extracting information from JWT tokens. */
  private final JwtService jwtService;

  /** Service for loading user details for authentication. */
  private final SecurityUserService securityUserService;

  /**
   * Checks the Authorization header for a Bearer token, validates it, and sets the security context
   * if authentication succeeds.
   *
   * @param request the incoming HTTP request
   * @param response the outgoing HTTP response
   * @param filterChain the chain of filters to continue processing the request
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;
    final String username;

    // If this request does not contain a Bearer token, continue the chain
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(7); // Remove "Bearer " prefix
    username = jwtService.extractUsername(jwtToken);

    // Authenticate the user if the token is valid and authentication is not yet set
    if (username != null) {
      if (SecurityContextHolder.getContext().getAuthentication() == null) {

        SecurityUser user = securityUserService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwtToken, user)) {

          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
