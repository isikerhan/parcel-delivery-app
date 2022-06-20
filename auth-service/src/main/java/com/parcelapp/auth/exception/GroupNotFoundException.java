package com.parcelapp.auth.exception;

import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends BusinessException {
	public GroupNotFoundException() {
		super(HttpStatus.NOT_FOUND, "Group not found!");
	}
}
