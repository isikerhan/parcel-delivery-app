package com.parcelapp.auth.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMemberToGroupRequest {
	@NotNull(message = "group is mandatory")
	private UserGroup group;
}
