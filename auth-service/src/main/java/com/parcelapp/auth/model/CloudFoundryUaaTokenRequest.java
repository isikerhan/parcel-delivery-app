package com.parcelapp.auth.model;

import feign.form.FormProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CloudFoundryUaaTokenRequest {
	@FormProperty("client_id")
	private String clientId;
	@FormProperty("client_secret")
	private String clientSecret;
	@FormProperty("grant_type")
	private String grantType;
	private String username;
	private String password;
	@FormProperty("token_format")
	private String tokenFormat;
}
