package com.parcelapp.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeExchange()
				.pathMatchers("/actuator/**").permitAll()
				.pathMatchers("/swagger-ui.html", "/webjars/swagger-ui/**", "/v3/api-docs/**", "/api/*/v3/api-docs/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/auth/token", "/api/auth/users").permitAll()
				.anyExchange().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt();

		return http.build();
	}

	@Bean
	public GlobalFilter loggingFilter() {
		return (exchange, chain) -> {
			Set<URI> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
			String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
			Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
			URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
			log.info("Incoming request " + originalUri + " is routed to id: " + route.getId()
					+ ", uri:" + routeUri);
			return chain.filter(exchange);
		};
	}
}
