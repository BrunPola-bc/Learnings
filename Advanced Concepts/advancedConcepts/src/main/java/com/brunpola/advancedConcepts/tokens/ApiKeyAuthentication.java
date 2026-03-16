package com.brunpola.advancedConcepts.tokens;

import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

  private final String apiKey;

  public ApiKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.apiKey = apiKey;
    setAuthenticated(true);
  }

  @Override
  public @Nullable Object getCredentials() {
    // No credentials for API Key authentication
    return null;
  }

  @Override
  public @Nullable Object getPrincipal() {
    return apiKey;
  }
}
