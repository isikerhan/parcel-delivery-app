package com.parcelapp.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CloudFoundryUaaCreateUserRequest {
	private String userName;
	private String password;
	private CloudFoundryUaaUser.Name name;
	private List<CloudFoundryUaaUser.Email> emails;
}
