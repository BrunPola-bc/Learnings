package com.brunpola.cv_management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configuration class for mapping setup between entitties and DTOs */
@Configuration
public class MapperConfig {

  /**
   * Configures and provides a {@link ModelMapper} with the MatchingStrategy set to {@link
   * MatchingStrategies#LOOSE} to allow slightly different field names between each.
   *
   * @return configured {@link ModelMapper}
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    return modelMapper;
  }
}
