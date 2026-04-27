package com.brunpola.people_service.domain.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ps_people")
public class PersonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;
  private String lastName;

  @ElementCollection
  @CollectionTable(name = "person_project_ids", joinColumns = @JoinColumn(name = "person_id"))
  @Column(name = "project_id")
  @Builder.Default
  private List<Long> projectIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "person_skill_ids", joinColumns = @JoinColumn(name = "person_id"))
  @Column(name = "skill_id")
  @Builder.Default
  private List<Long> skillIds = new ArrayList<>();
}
