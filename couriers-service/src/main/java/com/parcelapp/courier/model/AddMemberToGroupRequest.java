package com.parcelapp.courier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class AddMemberToGroupRequest {

	@NotNull(message = "group is mandatory")
	private UserGroup group;

	@RequiredArgsConstructor
	public enum UserGroup {
		COURIER
	}
}
