package com.brunpola.skills_service.service.impl;

import com.brunpola.skills_service.client.PeopleHttpClient;
import com.brunpola.skills_service.client.ProjectHttpClient;
import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.dto.SkillExtendedDto;
import com.brunpola.skills_service.domain.entity.SkillEntity;
import com.brunpola.skills_service.domain.external.PersonDto;
import com.brunpola.skills_service.domain.external.ProjectDto;
import com.brunpola.skills_service.exception.SkillNotFoundException;
import com.brunpola.skills_service.mapper.SkillMapper;
import com.brunpola.skills_service.repository.SkillRepository;
import com.brunpola.skills_service.service.SkillService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;
  private final SkillMapper skillMapper;
  private final PeopleHttpClient peopleClient;
  private final ProjectHttpClient projectClient;

  public SkillServiceImpl(
      SkillRepository skillRepository,
      SkillMapper skillMapper,
      PeopleHttpClient peopleClient,
      ProjectHttpClient projectClient) {
    this.skillRepository = skillRepository;
    this.skillMapper = skillMapper;
    this.peopleClient = peopleClient;
    this.projectClient = projectClient;
  }

  @Override
  public SkillDto save(SkillDto skillDto) {
    SkillEntity skillEntity = skillMapper.toEntity(skillDto);
    skillEntity = skillRepository.save(skillEntity);
    return skillMapper.toDto(skillEntity);
  }

  @Override
  public SkillDto update(SkillDto skillDto) {
    if (!isExists(skillDto.getId())) {
      throw new SkillNotFoundException(skillDto.getId());
    }

    SkillEntity skillEntity = skillMapper.toEntity(skillDto);
    skillEntity = skillRepository.save(skillEntity);
    return skillMapper.toDto(skillEntity);
  }

  @Override
  public List<SkillDto> findAll() {
    return skillRepository.findAll().stream().map(skillMapper::toDto).toList();
  }

  @Override
  public SkillDto findOne(Long id) {
    SkillEntity skillEntity =
        skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));
    return skillMapper.toDto(skillEntity);
  }

  @Override
  public boolean isExists(Long id) {
    return skillRepository.existsById(id);
  }

  @Override
  public SkillDto partialUpdate(Long id, SkillDto skillDto) {

    skillDto.setId(id);

    SkillEntity updatedSkillEntity =
        skillRepository
            .findById(id)
            .map(
                existingSkill -> {
                  Optional.ofNullable(skillDto.getSkillName())
                      .ifPresent(existingSkill::setSkillName);
                  return skillRepository.save(existingSkill);
                })
            .orElseThrow(() -> new SkillNotFoundException(id));

    return skillMapper.toDto(updatedSkillEntity);
  }

  @Override
  public void delete(Long id) {
    if (!isExists(id)) {
      throw new SkillNotFoundException(id);
    }
    skillRepository.deleteById(id);
  }

  @Override
  public SkillExtendedDto findOneExtended(Long id, String authHeader) {
    SkillEntity skillEntity =
        skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

    List<PersonDto> people = peopleClient.findPeopleBySkillId(id, authHeader);
    List<ProjectDto> projects = projectClient.findProjectsBySkillId(id, authHeader);

    return skillMapper.toExtendedDto(skillEntity, people, projects);
  }

  @Override
  public List<SkillExtendedDto> findAllExtended(String authHeader) {
    List<SkillEntity> skillEntities = skillRepository.findAll();

    return skillEntities.stream()
        .map(
            skillEntity -> {
              List<PersonDto> people =
                  peopleClient.findPeopleBySkillId(skillEntity.getId(), authHeader);
              List<ProjectDto> projects =
                  projectClient.findProjectsBySkillId(skillEntity.getId(), authHeader);

              return skillMapper.toExtendedDto(skillEntity, people, projects);
            })
        .toList();
  }

  @Override
  public List<SkillDto> findByIds(List<Long> ids) {
    List<SkillEntity> skills = skillRepository.findAllById(ids);
    return skills.stream().map(skillMapper::toDto).toList();
  }
}
