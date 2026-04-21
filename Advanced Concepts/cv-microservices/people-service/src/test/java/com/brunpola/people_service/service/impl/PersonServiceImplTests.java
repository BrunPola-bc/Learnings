package com.brunpola.people_service.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import java.util.List;
import java.util.Optional;
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
  void findOneExtended_shouldReturnDtoWithProjectsAndSkills() {

    // Mocked behavior
    when(personRepository.findById(1L)).thenReturn(Optional.of(personEntityWithId));
    when(projectClient.getProjectsByIds(List.of(1L))).thenReturn(listOfSampleProjectDto);
    when(skillClient.getSkillsByIds(List.of(1L))).thenReturn(listOfSampleSkillDto);
    when(personMapper.toExtendedDto(
            personEntityWithId, listOfSampleProjectDto, listOfSampleSkillDto))
        .thenReturn(personExtendedDtoWithId);

    // when
    PersonExtendedDto result = personService.findOneExtended(1L);

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
          personService.findOneExtended(1L);
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
    PersonExtendedDto result = personService.findOneExtended(1L);

    // then
    assertEquals(personExtendedDtoWithEmptyLists, result);

    verifyNoInteractions(projectClient);
    verifyNoInteractions(skillClient);
  }
}
