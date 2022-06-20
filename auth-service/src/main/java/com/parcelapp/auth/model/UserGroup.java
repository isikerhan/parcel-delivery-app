package com.parcelapp.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserGroup {
	COURIER("parcel_app.courier"), ADMIN("parcel_app.admin");

	@Getter
	private final String groupName;
}
