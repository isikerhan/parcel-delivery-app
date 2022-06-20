package com.parcelapp.courier.controller;

import com.parcelapp.courier.entity.Courier;
import com.parcelapp.courier.exception.CourierNotFoundException;
import com.parcelapp.courier.model.CourierModel;
import com.parcelapp.courier.model.CreateCourierModel;
import com.parcelapp.courier.model.CreateCourierResponseModel;
import com.parcelapp.courier.model.ErrorResponse;
import com.parcelapp.courier.service.CouriersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
@Tag(name = "Couriers Controller", description = "Operations related to couriers.")
@SecurityRequirement(name = "bearerAuth")
public class CouriersController {

	private final CouriersService couriersService;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@Operation(summary = "List all couriers.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listed couriers successfully.")
	})
	@PageableAsQueryParam
	public Page<CourierModel> getCouriers(
			@Parameter(description = "Courier type to filter.")
			@RequestParam(required = false) Courier.CourierType type,
			@Parameter(description = "Courier status to filter.")
			@RequestParam(required = false) Courier.CourierStatus status,
			@Parameter(hidden = true) Pageable pageable) {
		return couriersService.getCouriers(type, status, pageable);
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@Operation(summary = "Get courier by id.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the courier."),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Courier not found.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CourierModel getCourier(@Parameter(description = "Courier id.") @PathVariable Long id) throws CourierNotFoundException {
		return couriersService.getCourier(id);
	}

	@GetMapping(path = "/me", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.courier')")
	@Operation(summary = "Get courier associated with the current customer.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the courier."),
			@ApiResponse(responseCode = "404", description = "Courier not found.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CourierModel getOwnCourier() throws CourierNotFoundException {
		return couriersService.getOwnCourier();
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a courier and courier account.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Courier created successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid courier information supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CreateCourierResponseModel createCourier(@Valid @RequestBody CreateCourierModel createCourierModel) {
		return couriersService.createCourier(createCourierModel);
	}
}
