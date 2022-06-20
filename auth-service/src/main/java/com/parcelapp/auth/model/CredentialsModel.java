package com.parcelapp.auth.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CredentialsModel {
	@NotBlank(message = "user_name is mandatory")
	private String userName;
	@NotBlank(message = "password is mandatory")
	private String password;
}
