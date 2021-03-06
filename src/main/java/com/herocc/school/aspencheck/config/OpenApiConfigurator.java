package com.herocc.school.aspencheck.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfigurator {
  // https://www.dariawan.com/tutorials/spring/documenting-spring-boot-rest-api-springdoc-openapi-3/

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().components(new Components())
      .info(new Info()
        .title("Aspen WebScrape REST API")
        .license(new License().name("GPL-3.0").url("https://github.com/ClassyCreations/ScheduleCheck/blob/master/LICENSE")));
  }
}
