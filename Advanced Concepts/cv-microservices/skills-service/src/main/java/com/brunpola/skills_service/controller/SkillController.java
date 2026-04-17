package com.brunpola.skills_service.controller;

import com.brunpola.skills_service.domain.dto.SkillDto;
import com.brunpola.skills_service.domain.dto.SkillExtendedDto;
import com.brunpola.skills_service.service.SkillService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

  private final SkillService skillService;

  public SkillController(SkillService skillService) {
    this.skillService = skillService;
  }

  @PostMapping
  public ResponseEntity<SkillDto> createSkill(@RequestBody @Valid SkillDto skillDto) {
    SkillDto savedSkillDto = skillService.save(skillDto);
    return new ResponseEntity<>(savedSkillDto, HttpStatus.CREATED);
  }

  @GetMapping
  public List<SkillDto> listSkills() {
    return skillService.findAll();
  }

  @GetMapping(path = "/{id}")
  public SkillDto getSkill(@PathVariable("id") Long id) {
    return skillService.findOne(id);
  }

  @PutMapping("/{id}")
  public SkillDto fullUpdateSkill(@PathVariable("id") Long id, @RequestBody SkillDto skillDto) {

    skillDto.setId(id);
    return skillService.update(skillDto);
  }

  @PatchMapping(path = "/{id}")
  public SkillDto partialUpdateSkill(@PathVariable("id") Long id, @RequestBody SkillDto skillDto) {

    return skillService.partialUpdate(id, skillDto);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteSkill(@PathVariable("id") Long id) {
    skillService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/extended")
  public List<SkillExtendedDto> listSkillsExtended() {
    return skillService.findAllExtended();
  }

  @GetMapping("/{id}/extended")
  public SkillExtendedDto getSkillExtended(@PathVariable("id") Long id) {
    return skillService.findOneExtended(id);
  }

  @GetMapping("/by-ids")
  public List<SkillDto> getSkillsByIds(@RequestParam List<Long> ids) {
    return skillService.findByIds(ids);
  }
}
