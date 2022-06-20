package com.parcelapp.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
@OpenAPIDefinition(info = @Info(
		title = "Auth Service",
		description = "Parcel Delivery Application authentication & user management service.",
		version = "v1"))
@Slf4j
public class OpenApiConfiguration {
	@Bean
	public ModelResolver modelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}

	@Bean
	public OpenAPI openApi(@Value("${urls.gateway-public-url:#{null}}") String gatewayUrl,
						   @Value("${server.servlet.context-path:}") String contextPath) {
		OpenAPI openApi = new OpenAPI();
		if (StringUtils.hasLength(gatewayUrl)) {
			openApi.servers(List.of(new Server().url(gatewayUrl + contextPath).description("Server url through gateway")));
		}
		return openApi;
	}
}
