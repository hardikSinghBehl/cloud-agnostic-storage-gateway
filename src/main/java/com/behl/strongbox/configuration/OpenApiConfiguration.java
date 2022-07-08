package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.behl.strongbox.configuration.properties.OpenApiConfigurationProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;

@Configuration
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
@AllArgsConstructor
public class OpenApiConfiguration {

	private final OpenApiConfigurationProperties openApiConfigurationProperties;

	@Bean
	public OpenAPI customOpenAPI() {
		final var openApiProperties = openApiConfigurationProperties.getOpenApi();
		final var contact = openApiProperties.getContact();
		final var info = new Info().title(openApiProperties.getTitle()).version(openApiProperties.getApiVersion())
				.description(openApiProperties.getDescription())
				.contact(new Contact().email(contact.getEmail()).name(contact.getName()).url(contact.getUrl()));

		return new OpenAPI().info(info);
	}
}