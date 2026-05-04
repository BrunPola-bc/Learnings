package com.brunpola.auth_service.repositories;

import com.brunpola.auth_service.model.SecurityUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

  Optional<SecurityUser> findByEmail(String email);
}
