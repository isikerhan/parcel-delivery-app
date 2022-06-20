package com.parcelapp.auth.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parcelapp.auth.exception.BusinessException;
import com.parcelapp.auth.model.ErrorResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerExceptionHandler {

	private final ObjectMapper objectMapper;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		final HttpStatus status = e.getStatus();
		final ErrorResponse response = ErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(e.getMessage())
				.build();
		return ResponseEntity.status(status)
				.body(response);
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
		final HttpStatus status = HttpStatus.valueOf(e.status());
		final String content = e.contentUTF8();
		ErrorResponse cause = null;

		if (StringUtils.hasLength(content)) {
			try {
				cause = objectMapper.readValue(content, ErrorResponse.class);
			} catch (JsonProcessingException ex) {
				// error is not in the format of ErrorResponse
				// leave cause as null
			}
		}

		final ErrorResponse response = ErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.cause(cause)
				.build();
		return ResponseEntity.status(status)
				.body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
			MethodArgumentNotValidException ex) {

		final HttpStatus status = HttpStatus.BAD_REQUEST;
		final Map<String, List<String>> validationErrors = ex.getBindingResult().getAllErrors().stream()
				.collect(Collectors.groupingBy((err) -> err instanceof FieldError ? ((FieldError) err).getField() : "root",
						Collectors.mapping(ObjectError::getDefaultMessage, Collectors.toList())));

		final ErrorResponse response = ErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("Validation error!")
				.validationErrors(validationErrors)
				.build();
		return ResponseEntity.status(status)
				.body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleMessageNotReadableException() {
		final HttpStatus status = HttpStatus.BAD_REQUEST;
		final ErrorResponse response = ErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("Bad request body!")
				.build();
		return ResponseEntity.status(status)
				.body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAccessDeniedException() {
		// do nothing
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ErrorResponse> handleGenericError(Throwable t) {
		log.error("Uncaught error is caught in controller advice.", t);

		final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		final ErrorResponse response = ErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("An error occurred! Please try again later.")
				.build();
		return ResponseEntity.status(status)
				.body(response);
	}
}
