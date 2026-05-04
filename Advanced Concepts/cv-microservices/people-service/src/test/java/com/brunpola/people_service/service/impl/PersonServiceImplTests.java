package com.brunpola.people_service.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.brunpola.people_service.TestDataUtil;
import com.brunpola.people_service.client.ProjectHttpClient;
import com.brunpola.people_service.client.SkillHttpClient;
import com.brunpola.people_service.domain.dto.PersonDto;
import com.brunpola.people_service.domain.dto.PersonExtendedDto;
import com.brunpola.people_service.domain.entity.PersonEntity;
import com.brunpola.people_service.domain.external.ProjectDto;
import com.brunpola.people_service.domain.external.SkillDto;
import com.brunpola.people_service.exception.PersonNotFoundException;
import com.brunpola.people_service.mapper.PersonMapper;
import com.brunpola.people_service.repository.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTests {

  @Mock private PersonRepository personRepository;
  @Mock private ProjectHttpClient projectClient;
  @Mock private SkillHttpClient skillClient;
  @Mock private PersonMapper personMapper;

  @InjectMocks private PersonServiceImpl personService;

  private static final String FAKE_TOKEN = "fake token";

  private final PersonDto personDtoNoId = TestDataUtil.samplePersonDto(false);
  private final PersonEntity personEntityNoId = TestDataUtil.samplePersonEntity(false);
  private final PersonEntity personEntityWithId = TestDataUtil.samplePersonEntity(true);
  private final PersonDto personDtoWithId = TestDataUtil.samplePersonDto(true);
  private final List<ProjectDto> listOfSampleProjectDto = List.of(TestDataUtil.sampleProjectDto());
  private final List<SkillDto> listOfSampleSkillDto = List.of(TestDataUtil.sampleSkillDto());
  private final PersonExtendedDto personExtendedDtoWithId =
      TestDataUtil.samplePersonExtendedDto(true);

  @Test
  void save_shouldReturnDtoWithId() {

    // Mocked behavior
    when(personRepository.save(personEntityNoId)).thenReturn(personEntityWithId);
    when(personMapper.toEntity(personDtoNoId)).thenReturn(personEntityNoId);
    when(personMapper.toDto(personEntityWithId)).thenReturn(personDtoWithId);

    // when
    PersonDto result = personService.save(personDtoNoId);

    // then
    assertEquals(personDtoWithId, result);

    // Optional (it is implicitly tested by the above assertion)
    verify(personMapper).toEntity(personDtoNoId);
    verify(personRepository).save(personEntityNoId);
    verify(personMapper).toDto(personEntityWithId);
  }

  @Test
  void update_shouldThrowWhenPersonDoesNotExist() {

    // Mocked behaviour
    when(personRepository.existsById(1L)).thenReturn(false);

    // when & then
    assertThrows(
        PersonNotFoundException.class,
        () -> {
          personService.update(personDtoWithId);
        });

    verify(personRepository).existsById(1L);
    verifyNoMoreInteractions(personRepository);
    verifyNoInteractions(personMapper);
  }

  @Test
  void update_shouldThrowWhenNoIdProvided() {

    // Mocked behaviour
    when(personRepository.existsById(null)).thenReturn(false);

    // when & then
    assertThrows(
        PersonNotFoundException.class,
        () -> {
          personService.update(personDtoNoId);
        });

    verify(personRepository).existsById(null);
    verifyNoMoreInteractions(personRepository);
    verifyNoInteractions(personMapper);
  }

  @Test
  void update_shouldReturnUpdatedDto() {

    // Mocked behaviour
    when(personRepository.existsById(1L)).thenReturn(true);
    when(personMapper.toEntity(personDtoWithId)).thenReturn(personEntityWithId);
    when(personRepository.save(personEntityWithId)).thenReturn(personEntityWithId);
    when(personMapper.toDto(personEntityWithId)).thenReturn(personDtoWithId);

    // when
    PersonDto result = personService.update(personDtoWithId);

    // then
    assertEquals(personDtoWithId, result);
  }

  @Test
  void findAll_shouldReturnListOfDtos() {

    // given
    final int COUNT = 2;
    List<PersonEntity> entities = TestDataUtil.samplePeopleEntities(true, COUNT);
    List<PersonDto> dtos = TestDataUtil.samplePeopleDtos(true, COUNT);

    // Mocked behavior
    when(personRepository.findAll()).thenReturn(entities);
    for (int i = 0; i < COUNT; i++) {
      when(personMapper.toDto(entities.get(i))).thenReturn(dtos.get(i));
    }

    // when
    List<PersonDto> result = personService.findAll();

    // then
    assertEquals(dtos, result);

    verify(personRepository).findAll();
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoData() {

    // mocked behavior
    when(personRepository.findAll()).thenReturn(List.of());

    // when
    List<PersonDto> result = personService.findAll();

    // then
    assertEquals(List.of(), result);

    verify(personRepository).findAll();
    verifyNoInteractions(personMapper);
  }

  @Test
  void findOne_shouldReturnDto() {

    // mocked behaviour
    when(personRepository.findById(1L)).thenReturn(Optional.of(personEntityWithId));
    when(personMapper.toDto(personEntityWithId)).thenReturn(personDtoWithId);

    // when
    PersonDto result = personService.findOne(1L);

    // then
    assertEquals(result, personDtoWithId);
  }

  @Test
  void findOne_shouldThrowWhenPersonNotFound() {

    // mocked behaviour
    when(personRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        PersonNotFoundException.class,
        () -> {
          personService.findOne(1L);
        });

    verifyNoInteractions(personMapper);
  }

  @Test
  void delete_shouldThrowWhenPersonNotFound() {

    // mocked behaviour
    when(personRepository.existsById(1L)).thenReturn(false);

    // when & then
    assertThrows(
        PersonNotFoundException.class,
        () -> {
          personService.delete(1L);
        });

    verifyNoMoreInteractions(personRepository);
  }

  @Test
  void delete_shouldNotThrowWhenPersonExists() {

    // mocked behaviour
    when(personRepository.existsById(1L)).thenReturn(true);

    // when & then
    assertDoesNotThrow(() -> personService.delete(1L));

    verify(personRepository).deleteById(1L);
  }

  @Test
  void partialUpdate_shouldReturnUpdatedDto() {

    // Given
    PersonEntity existing = TestDataUtil.samplePersonEntity(true);

    PersonDto updateDto = new PersonDto();
    updateDto.setFirstName("New");
    updateDto.setLastName("Updated");

    PersonDto expectedDto = new PersonDto(1L, "New", "Updated");

    when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(personRepository.save(existing)).thenReturn(existing);
    when(personMapper.toDto(existing)).thenReturn(expectedDto);

    // when
    PersonDto result = personService.partialUpdate(1L, updateDto);

    // then
    assertEquals(expectedDto, result);
    assertEquals("New", result.getFirstName());
    assertEquals("Updated", result.getLastName());

    verify(personRepository).save(existing);
  }

  @Test
  void partialUpdate_shouldReturnPartiallyUpdatedDto() {

    // Given
    PersonEntity existing = TestDataUtil.samplePersonEntity(true);

    PersonDto updateDto = new PersonDto();
    updateDto.setFirstName("New");

    PersonDto expectedDto = new PersonDto(1L, "New", "Last Name");

    when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(personRepository.save(existing)).thenReturn(existing);
    when(personMapper.toDto(existing)).thenReturn(expectedDto);

    // when
    PersonDto result = personService.partialUpdate(1L, updateDto);

    // then
    assertEquals(expectedDto, result);
    assertEquals("New", result.getFirstName());
    assertEquals("Last Name", result.getLastName());

    verify(personRepository).save(existing);
  }

  @Test
  void partialUpdate_shouldThrowWhenPersonNotFound() {

    // given
    PersonDto emptyDto = new PersonDto();

    // mocked behaviour
    when(personRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(PersonNotFoundException.class, () -> personService.partialUpdate(1L, emptyDto));

    verify(personRepository).findById(1L);
    verifyNoInteractions(personMapper);
  }

  @Test
  void partialUpdate_shouldReturnUnchangedDtoWhenDtoIsEmpty() {

    // Given
    PersonEntity existing = TestDataUtil.samplePersonEntity(true);

    PersonDto updateDto = new PersonDto();

    PersonDto expectedDto = new PersonDto(1L, "Person", "Last Name");

    when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(personRepository.save(existing)).thenReturn(existing);
    when(personMapper.toDto(existing)).thenReturn(expectedDto);

    // when
    PersonDto result = personService.partialUpdate(1L, updateDto);

    // then
    assertEquals(expectedDto, result);
    assertEquals("Person", result.getFirstName());
    assertEquals("Last Name", result.getLastName());

    verify(personRepository).save(existing);
  }

  @Test
  void updateSkills_shouldUpdateSkills() {

    // Given
    PersonEntity existing = TestDataUtil.samplePersonEntity(true);
    existing.setSkillIds(List.of(1L));

    List<Long> newSkills = List.of(2L, 3L);

    PersonEntity saved = TestDataUtil.samplePersonEntity(true);
    saved.setSkillIds(newSkills);

    PersonDto expectedDto = TestDataUtil.samplePersonDto(true);

    when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(personRepository.save(existing)).thenReturn(saved);
    when(personMapper.toDto(saved)).thenReturn(expectedDto);

    // When
    PersonDto result = personService.updateSkills(1L, newSkills);

    // Then
    assertEquals(expectedDto, result);

    verify(personRepository).findById(1L);
    verify(personRepository).save(existing);

    assertEquals(newSkills, existing.getSkillIds());
  }

  @Test
  void updateProjects_shouldUpdateProjects() {

    // Given
    PersonEntity existing = TestDataUtil.samplePersonEntity(true);
    existing.setProjectIds(List.of(1L));

    List<Long> newProjects = List.of(10L, 20L);

    PersonEntity saved = TestDataUtil.samplePersonEntity(true);
    saved.setProjectIds(newProjects);

    PersonDto expectedDto = TestDataUtil.samplePersonDto(true);

    when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(personRepository.save(existing)).thenReturn(saved);
    when(personMapper.toDto(saved)).thenReturn(expectedDto);

    // When
    PersonDto result = personService.updateProjects(1L, newProjects);

    // Then
    assertEquals(expectedDto, result);

    verify(personRepository).findById(1L);
    verify(personRepository).save(existing);

    assertEquals(newProjects, existing.getProjectIds());
  }

  @Test
  void findBySkillId_shouldReturnPersonDtos() {

    // Given
    Long skillId = 1L;

    List<PersonEntity> entities = TestDataUtil.samplePeopleEntities(true, 2);
    List<PersonDto> dtos = TestDataUtil.samplePeopleDtos(true, 2);

    when(personRepository.findBySkillIdsContaining(skillId)).thenReturn(entities);
    when(personMapper.toDto(entities.get(0))).thenReturn(dtos.get(0));
    when(personMapper.toDto(entities.get(1))).thenReturn(dtos.get(1));

    // When
    List<PersonDto> result = personService.findBySkillId(skillId);

    // Then
    assertEquals(2, result.size());
    assertEquals(dtos, result);

    verify(personRepository).findBySkillIdsContaining(skillId);
    verify(personMapper).toDto(entities.get(0));
    verify(personMapper).toDto(entities.get(1));
  }

  @Test
  void findBySkillId_shouldReturnEmptyList() {

    Long skillId = 1L;

    when(personRepository.findBySkillIdsContaining(skillId)).thenReturn(List.of());

    List<PersonDto> result = personService.findBySkillId(skillId);

    assertTrue(result.isEmpty());

    verify(personRepository).findBySkillIdsContaining(skillId);
    verifyNoInteractions(personMapper);
  }

  @Test
  void findByProjectId_shouldReturnPersonDtos() {

    // Given
    Long projectId = 1L;

    List<PersonEntity> entities = TestDataUtil.samplePeopleEntities(true, 2);
    List<PersonDto> dtos = TestDataUtil.samplePeopleDtos(true, 2);

    when(personRepository.findByProjectIdsContaining(projectId)).thenReturn(entities);
    when(personMapper.toDto(entities.get(0))).thenReturn(dtos.get(0));
    when(personMapper.toDto(entities.get(1))).thenReturn(dtos.get(1));

    // When
    List<PersonDto> result = personService.findByProjectId(projectId);

    // Then
    assertEquals(2, result.size());
    assertEquals(dtos, result);

    verify(personRepository).findByProjectIdsContaining(projectId);
    verify(personMapper).toDto(entities.get(0));
    verify(personMapper).toDto(entities.get(1));
  }

  @Test
  void findByProjectId_shouldReturnEmptyList() {

    Long projectId = 10L;

    when(personRepository.findByProjectIdsContaining(projectId)).thenReturn(List.of());

    List<PersonDto> result = personService.findByProjectId(projectId);

    assertTrue(result.isEmpty());

    verify(personRepository).findByProjectIdsContaining(projectId);
    verifyNoInteractions(personMapper);
  }

  @Test
  void findOneExtended_shouldReturnDtoWithProjectsAndSkills() {

    // Mocked behavior
    when(personRepository.findById(1L)).thenReturn(Optional.of(personEntityWithId));
    when(projectClient.getProjectsByIds(List.of(1L), FAKE_TOKEN))
        .thenReturn(listOfSampleProjectDto);
    when(skillClient.getSkillsByIds(List.of(1L), FAKE_TOKEN)).thenReturn(listOfSampleSkillDto);
    when(personMapper.toExtendedDto(
            personEntityWithId, listOfSampleProjectDto, listOfSampleSkillDto))
        .thenReturn(personExtendedDtoWithId);

    // when
    PersonExtendedDto result = personService.findOneExtended(1L, FAKE_TOKEN);

    // then
    assertEquals(result, personExtendedDtoWithId);
  }

  @Test
  void findOneExtended_shouldThrowWhenPersonNotFound() {

    // mocked behavior
    when(personRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(
        PersonNotFoundException.class,
        () -> {
          personService.findOneExtended(1L, FAKE_TOKEN);
        });

    verify(personRepository).findById(1L);
    verifyNoInteractions(projectClient, skillClient, personMapper);
  }

  @Test
  void findOneExtended_shouldReturnDtoWithEmptyProjectsAndSkills() {

    // Given
    PersonEntity personEntityWithEmptyLists = TestDataUtil.samplePersonEntityWithEmptyLists(true);
    PersonExtendedDto personExtendedDtoWithEmptyLists =
        TestDataUtil.samplePersonExtendedDtoWithEmptyLists(true);

    // Mocked behavior
    when(personRepository.findById(1L)).thenReturn(Optional.of(personEntityWithEmptyLists));
    when(personMapper.toExtendedDto(personEntityWithEmptyLists, List.of(), List.of()))
        .thenReturn(personExtendedDtoWithEmptyLists);

    // when
    PersonExtendedDto result = personService.findOneExtended(1L, FAKE_TOKEN);

    // then
    assertEquals(personExtendedDtoWithEmptyLists, result);

    verifyNoInteractions(projectClient);
    verifyNoInteractions(skillClient);
  }

  @Test
  void findAllExtended_shouldReturnExtendedDtos() {

    // Given
    PersonEntity p1 = TestDataUtil.samplePersonEntity(true);
    p1.setProjectIds(List.of(1L));
    p1.setSkillIds(List.of(10L));

    PersonEntity p2 = TestDataUtil.samplePersonEntity(true);
    p2.setProjectIds(List.of(2L));
    p2.setSkillIds(List.of(20L));

    List<PersonEntity> people = List.of(p1, p2);

    ProjectDto project1 = TestDataUtil.sampleProjectDto();
    project1.setId(1L);

    ProjectDto project2 = TestDataUtil.sampleProjectDto();
    project2.setId(2L);

    SkillDto skill10 = TestDataUtil.sampleSkillDto();
    skill10.setId(10L);

    SkillDto skill20 = TestDataUtil.sampleSkillDto();
    skill20.setId(20L);

    PersonExtendedDto dto1 = TestDataUtil.samplePersonExtendedDto(true);
    PersonExtendedDto dto2 = TestDataUtil.samplePersonExtendedDto(true);

    // mocked behaviour
    when(personRepository.findAll()).thenReturn(people);
    when(projectClient.getProjectsByIds(
            argThat(list -> list.containsAll(List.of(1L, 2L)) && list.size() == 2), eq(FAKE_TOKEN)))
        .thenReturn(List.of(project1, project2));
    when(skillClient.getSkillsByIds(
            argThat(list -> list.containsAll(List.of(10L, 20L)) && list.size() == 2),
            eq(FAKE_TOKEN)))
        .thenReturn(List.of(skill10, skill20));
    when(personMapper.toExtendedDto(p1, List.of(project1), List.of(skill10))).thenReturn(dto1);
    when(personMapper.toExtendedDto(p2, List.of(project2), List.of(skill20))).thenReturn(dto2);

    // When
    List<PersonExtendedDto> result = personService.findAllExtended(FAKE_TOKEN);

    // Then
    assertEquals(2, result.size());
    assertEquals(List.of(dto1, dto2), result);

    verify(personRepository).findAll();
    verify(projectClient).getProjectsByIds(anyList(), eq(FAKE_TOKEN));
    verify(skillClient).getSkillsByIds(anyList(), eq(FAKE_TOKEN));
  }

  @Test
  void findAllExtended_shouldReturnEmptyList() {

    when(personRepository.findAll()).thenReturn(List.of());

    List<PersonExtendedDto> result = personService.findAllExtended(FAKE_TOKEN);

    assertTrue(result.isEmpty());

    verify(personRepository).findAll();
    verify(projectClient).getProjectsByIds(new ArrayList<>(Set.of()), FAKE_TOKEN);
    verifyNoMoreInteractions(projectClient);
    verify(skillClient).getSkillsByIds(new ArrayList<>(Set.of()), FAKE_TOKEN);
    verifyNoMoreInteractions(skillClient);
    verifyNoInteractions(personMapper);
  }

  @Test
  void findByIds_shouldReturnPersonDtos() {

    // Given
    List<Long> ids = List.of(1L, 3L);

    List<PersonEntity> entities = TestDataUtil.samplePeopleEntities(true, 3);
    List<PersonEntity> filteredEntities = List.of(entities.get(0), entities.get(2));

    List<PersonDto> dtos = TestDataUtil.samplePeopleDtos(true, 3);
    dtos.remove(1);

    when(personRepository.findAllById(ids)).thenReturn(filteredEntities);

    when(personMapper.toDto(entities.get(0))).thenReturn(dtos.get(0));
    when(personMapper.toDto(entities.get(2))).thenReturn(dtos.get(1));

    // When
    List<PersonDto> result = personService.findByIds(ids);

    // Then
    assertEquals(2, result.size());
    assertEquals(dtos, result);

    verify(personRepository).findAllById(ids);
    verify(personMapper).toDto(entities.get(0));
    verify(personMapper, never()).toDto(entities.get(1));
    verify(personMapper).toDto(entities.get(2));
  }
}
