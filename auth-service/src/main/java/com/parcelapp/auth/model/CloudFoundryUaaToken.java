package com.parcelapp.auth.model;

import lombok.Data;

@Data
public class CloudFoundryUaaToken {
	private String accessToken;
	private String tokenType;
	private String idToken;
	private long expiresIn;
	private String scope;
	private String jti;
}
