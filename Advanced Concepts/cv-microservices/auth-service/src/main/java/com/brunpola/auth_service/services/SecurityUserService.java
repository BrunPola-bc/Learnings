package com.brunpola.auth_service.services;

import com.brunpola.auth_service.model.SecurityUser;
import com.brunpola.auth_service.repositories.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

  private final SecurityUserRepository repository;

  @Override
  public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
