package com.brunpola.cv_management.dao;

import com.brunpola.cv_management.domain.Person;
import java.util.List;
import java.util.Optional;

public interface PersonDao {

  Person create(Person person);

  Optional<Person> findOne(long personId);

  List<Person> find();

  void update(Person person);

  void delete(long personId);
}
