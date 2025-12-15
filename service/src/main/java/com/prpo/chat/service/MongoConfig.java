package com.prpo.chat.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

  @Bean
  @Primary
  public MappingMongoConverter mappingMongoConverter(
      final MongoDatabaseFactory factory,
      final MongoMappingContext context) {
    final var converter =
        new MappingMongoConverter(new DefaultDbRefResolver(factory), context);

    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    return converter;
  }

}
