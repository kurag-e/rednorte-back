package com.rednorte.adminred.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig { 
    @Bean 
    public OpenAPI api(){ 
    return new OpenAPI().info(new Info().title("MS Administracion de Red - RedNorte").version("1.0.0").description("Gestion de hospitales, CAPS, clinicas y especialidades de la red de salud")); 
    } 
}
