package com.brunpola.cv_management.security.filters;

// import jakarta.annotation.Nonnull;
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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final SecurityUserService securityUserService;

  @Override
  protected void doFilterInternal(
      /* @Nonnull */ HttpServletRequest request,
      /* @Nonnull */ HttpServletResponse response,
      /* @Nonnull */ FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;
    final String username;

    // If this clause is true: this isnt a request with a JWT token
    // => we continue to the next filter (because this one is for JWT authentication)
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(7); // After the "Bearer " prefix

    username = jwtService.extractUsername(jwtToken);

    // If username was found in the token and the user is not yet authenticated
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      SecurityUser user = securityUserService.loadUserByUsername(username);

      if (jwtService.isTokenValid(jwtToken, user)) {

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    // doFilter(request, response, filterChain);
    filterChain.doFilter(request, response);
  }
}
