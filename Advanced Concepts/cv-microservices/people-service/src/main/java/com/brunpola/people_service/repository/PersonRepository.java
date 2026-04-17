package com.brunpola.people_service.repository;

import com.brunpola.people_service.domain.entity.PersonEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

  List<PersonEntity> findByProjectIdsContaining(Long projectId);

  List<PersonEntity> findBySkillIdsContaining(Long skillId);
}
