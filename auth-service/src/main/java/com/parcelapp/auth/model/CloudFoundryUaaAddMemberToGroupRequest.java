package com.parcelapp.auth.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CloudFoundryUaaAddMemberToGroupRequest {
	private String value;
	private String type;
	private String origin;
}
