package com.brunpola.cv_management.security.repositories;

import com.brunpola.cv_management.security.model.SecurityUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link SecurityUser} entities.
 *
 * <p>Provides CRUD operations and a query method to look up users by email.
 */
@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

  /**
   * Finds a user by their email address.
   *
   * @param email the email to search for
   * @return an {@link Optional} containing the {@link SecurityUser} if found, or empty otherwise
   */
  Optional<SecurityUser> findByEmail(String email);
}
