package com.parcelapp.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CloudFoundryUaaUser {
	private String id;
	private String userName;
	private Name name;
	private List<Email> emails;

	@Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@Builder
	@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
	public static class Name {
		private String formatted;
		private String familyName;
		private String givenName;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
	public static class Email {
		private String value;
		private boolean primary;
	}
}
