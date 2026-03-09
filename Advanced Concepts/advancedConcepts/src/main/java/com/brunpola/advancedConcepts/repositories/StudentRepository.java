package com.brunpola.advancedConcepts.repositories;

import com.brunpola.advancedConcepts.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {}
