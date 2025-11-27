package com.brunpola.cv_management.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "People")
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // This should work for my 'week1database'
  @Column(name = "ID")
  private Long id;

  @Column(name = "FirstName")
  private String firstName;

  @Column(name = "LastName")
  private String lastName;
}
