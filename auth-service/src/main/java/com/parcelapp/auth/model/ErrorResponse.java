package com.parcelapp.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
	private int status;
	private String error;
	private String message;
	@Builder.Default
	private ZonedDateTime time = ZonedDateTime.now();
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ErrorResponse cause;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, List<String>> validationErrors;
}
