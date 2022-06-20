package com.parcelapp.courier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfiguration {

	@Bean
	public DateTimeProvider auditingDateTimeProvider() {
		return () -> Optional.of(LocalDateTime.now());
	}

	@Bean
	public AuditorAware<String> securityAuditorAware() {
		return () -> {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication instanceof JwtAuthenticationToken)) {
				return Optional.empty();
			}
			final Jwt credentials = (Jwt) authentication.getCredentials();
			return Optional.ofNullable(credentials.getClaim("user_id"));
		};
	}
}
