package com.parcelapp.courier.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateUserModel {
	@NotBlank(message = "user_name is mandatory")
	private String userName;
	@NotBlank(message = "password is mandatory")
	private String password;
	@NotBlank(message = "first_name is mandatory")
	private String firstName;
	@NotBlank(message = "last_name is mandatory")
	private String lastName;
	@NotBlank(message = "email_address is mandatory")
	private String emailAddress;
}
