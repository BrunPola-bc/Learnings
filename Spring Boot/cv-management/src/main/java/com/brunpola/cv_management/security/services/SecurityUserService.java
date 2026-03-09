package com.brunpola.cv_management.security.services;

import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.repositories.SecurityUserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService implements UserDetailsService {

  private final SecurityUserRepository repository;

  public SecurityUserService(SecurityUserRepository repository) {
    this.repository = repository;
  }

  @Override
  public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByEmail(username).orElseThrow();
  }
}
