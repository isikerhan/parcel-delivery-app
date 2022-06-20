package com.parcelapp.auth.model;

import lombok.Data;

@Data
public class TokenModel {
	private String accessToken;
	private String tokenType;
	private String idToken;
	private long expiresIn;
}
