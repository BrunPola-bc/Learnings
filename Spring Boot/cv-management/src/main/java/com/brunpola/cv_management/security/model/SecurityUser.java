package com.brunpola.cv_management.security.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity // <---
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

  @Id @GeneratedValue private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;

  // @Enumerated(EnumType.STRING)
  // private Role role;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    // return List.of(new SimpleGrantedAuthority(role.name()));

    return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
  }

  @Override
  public String getUsername() {
    // In this implementation username == email
    return email;
  }

  @Override
  public @Nullable String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    // Just calls super for now, but keeping it here in case I want to make changes later
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    // Just calls super for now, but keeping it here in case I want to make changes later
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // Just calls super for now, but keeping it here in case I want to make changes later
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    // Just calls super for now, but keeping it here in case I want to make changes later
    return UserDetails.super.isEnabled();
  }
}
