package com.parcelapp.auth.model;

import lombok.Data;

@Data
public class UserModel {
	private String id;
	private String userName;
	private String firstName;
	private String lastName;
	private String emailAddress;
}
