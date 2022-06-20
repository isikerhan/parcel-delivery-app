package com.parcelapp.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity-service")
@Data
public class IdentityServiceConfigurationProperties {
	private String clientId;
	private String clientSecret;
}
