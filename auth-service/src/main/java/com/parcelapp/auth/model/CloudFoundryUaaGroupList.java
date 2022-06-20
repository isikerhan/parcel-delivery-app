package com.parcelapp.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CloudFoundryUaaGroupList {
	private List<CloudFoundryUaaGroup> resources;

	@Data
	@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
	public static class CloudFoundryUaaGroup {
		private String id;
		private String displayName;
	}
}
