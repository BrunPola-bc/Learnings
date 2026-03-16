package com.brunpola.advancedConcepts.filters;

import com.brunpola.advancedConcepts.services.ApiKeyAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends GenericFilterBean {

  private final ApiKeyAuthenticationService apiKeyAuthenticationService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    try {

      if (SecurityContextHolder.getContext().getAuthentication() != null) {
        filterChain.doFilter(request, response);
        return;
      }

      Authentication auth =
          apiKeyAuthenticationService.getAuthentication((HttpServletRequest) request);

      if (auth != null) {
        SecurityContextHolder.getContext().setAuthentication(auth);
      }

      filterChain.doFilter(request, response);

    } catch (Exception exp) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
      PrintWriter writer = httpResponse.getWriter();
      writer.print(exp.getMessage());
      writer.flush();
      writer.close();
    }
  }
}
