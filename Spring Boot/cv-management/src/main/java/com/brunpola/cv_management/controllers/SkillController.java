package com.brunpola.cv_management.controllers;

import com.brunpola.cv_management.domain.dto.SkillDto;
import com.brunpola.cv_management.domain.entities.SkillEntity;
import com.brunpola.cv_management.mappers.Mapper;
import com.brunpola.cv_management.services.SkillService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkillController {

  private final SkillService skillService;
  private final Mapper<SkillEntity, SkillDto> skillMapper;

  public SkillController(SkillService skillService, Mapper<SkillEntity, SkillDto> skillMapper) {
    this.skillService = skillService;
    this.skillMapper = skillMapper;
  }

  @PostMapping(path = "/skills")
  public ResponseEntity<SkillDto> createSkill(@RequestBody SkillDto skillDto) {
    SkillEntity skillEntity = skillMapper.mapFrom(skillDto);
    SkillEntity savedSkillEntity = skillService.createSkill(skillEntity);
    return new ResponseEntity<>(skillMapper.mapTo(savedSkillEntity), HttpStatus.CREATED);
  }

  @GetMapping("/skills")
  public List<SkillDto> listSkills() {
    List<SkillEntity> skills = skillService.findAll();
    return skills.stream().map(skillMapper::mapTo).toList();
  }
}
