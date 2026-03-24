package com.brunpola.cv_management.security.services;

import com.brunpola.cv_management.security.model.SecurityUser;
import com.brunpola.cv_management.security.repositories.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link SecurityUser} entities and loading user details for Spring Security.
 *
 * <p>Implements {@link UserDetailsService} to provide authentication support via email.
 */
@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

  /** Repository used for accessing {@link SecurityUser} entities. */
  private final SecurityUserRepository repository;

  /**
   * Loads a {@link SecurityUser} by their email address.
   *
   * @param username the email of the user
   * @return the {@link SecurityUser} corresponding to the given email
   * @throws UsernameNotFoundException if no user is found with the given email
   */
  @Override
  public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
