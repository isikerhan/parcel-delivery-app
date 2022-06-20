package com.parcelapp.order.controller;

import com.parcelapp.order.exception.BusinessException;
import com.parcelapp.order.exception.CourierNotFoundException;
import com.parcelapp.order.exception.OrderNotFoundException;
import com.parcelapp.order.model.CreateDeliveryMovementModel;
import com.parcelapp.order.model.CreateDeliveryOrderModel;
import com.parcelapp.order.model.DeliveryMovementModel;
import com.parcelapp.order.model.DeliveryOrderModel;
import com.parcelapp.order.model.DetailedDeliveryOrderModel;
import com.parcelapp.order.model.ErrorResponse;
import com.parcelapp.order.model.UpdateDeliveryOrderAssigneeModel;
import com.parcelapp.order.model.UpdateDeliveryOrderDestinationModel;
import com.parcelapp.order.model.UpdateDeliveryOrderStatusModel;
import com.parcelapp.order.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders Controller", description = "Operations related to delivery orders.")
@SecurityRequirement(name = "bearerAuth")
public class OrdersController {
	private final OrdersService ordersService;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@Operation(summary = "List delivery orders.", parameters = {
			@Parameter(name = "user_id", description = "When set to 'me', the service returns the orders " +
					"created by the current user.", in = ParameterIn.QUERY),
			@Parameter(name = "assigned_courier_id", description = "When set to 'me', the service returns the orders " +
					"assigned to the courier associated with the current user.", in = ParameterIn.QUERY)
	})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listed delivery orders successfully.")
	})
	@PageableAsQueryParam
	public Page<DeliveryOrderModel> getDeliveryOrders(@Parameter(hidden = true) Pageable pageable) {
		return ordersService.getDeliveryOrders(pageable);
	}

	@GetMapping(params = "user_id=me", produces = APPLICATION_JSON_VALUE)
	@Operation
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listed delivery orders created by the current user successfully.")
	})
	public Page<DeliveryOrderModel> getOwnDeliveryOrders(@Parameter(hidden = true) Pageable pageable) {
		return ordersService.getOwnDeliveryOrders(pageable);
	}

	@GetMapping(params = "assigned_courier_id=me", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.courier')")
	@Operation
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					description = "Listed delivery orders assigned to the courier associated with the current user successfully.")
	})
	public Page<DeliveryOrderModel> getAssignedDeliveryOrders(@Parameter(hidden = true) Pageable pageable) throws CourierNotFoundException {
		return ordersService.getAssignedDeliveryOrders(pageable);
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get delivery order by id.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the delivery order."),
			@ApiResponse(responseCode = "404", description = "Delivery order not found.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DetailedDeliveryOrderModel getDeliveryOrder(
			@Parameter(description = "Delivery order id.") @PathVariable Long id,
			JwtAuthenticationToken jwtAuthenticationToken) throws OrderNotFoundException {
		return ordersService.getDeliveryOrder(id, jwtAuthenticationToken);
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create delivery order.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Delivery order created successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid delivery order info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DeliveryOrderModel createDeliveryOrder(@Valid @RequestBody CreateDeliveryOrderModel createDeliveryOrderModel) {
		return ordersService.createDeliveryOrder(createDeliveryOrderModel);
	}

	@PostMapping(path = "/{id}/movements", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@Operation(summary = "Create delivery movement on a delivery order.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Delivery movement created successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid delivery movement info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryMovementModel createDeliveryMovement(
			@Parameter(description = "Id of delivery order to be associated with the movement")
			@PathVariable("id") Long orderId,
			@Valid @RequestBody CreateDeliveryMovementModel createDeliveryMovementModel) throws OrderNotFoundException {
		return ordersService.createDeliveryMovement(orderId, createDeliveryMovementModel);
	}

	@PatchMapping(path = "/{id}/destination", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Update destination of a delivery order.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Destination of the delivery order updated successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid destination info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Status of the delivery order is not appropriate for updating destination.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DeliveryOrderModel updateDeliveryOrderDestination(
			@Parameter(description = "Delivery order id") @PathVariable Long id,
			@Valid @RequestBody UpdateDeliveryOrderDestinationModel updateDeliveryOrderModel) throws BusinessException {
		return ordersService.updateDeliveryOrderDestination(id, updateDeliveryOrderModel);
	}

	@PatchMapping(path = "/{id}/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Update status of a delivery order.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Status of the delivery order updated successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid status info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Status of the delivery order is not appropriate for updating status.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DeliveryOrderModel updateDeliveryOrderStatus(
			@Parameter(description = "Delivery order id") @PathVariable Long id,
			@Valid @RequestBody UpdateDeliveryOrderStatusModel updateDeliveryOrderStatusModel,
			JwtAuthenticationToken jwtAuthenticationToken) throws BusinessException {
		return ordersService.updateDeliveryOrderStatus(id, updateDeliveryOrderStatusModel, jwtAuthenticationToken);
	}

	@PatchMapping(path = "/{id}/assigned-courier", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@Operation(summary = "Update assigned courier of a delivery order.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Assigned courier of the delivery order updated successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid courier info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Specified courier is not found.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DeliveryOrderModel updateDeliveryOrderAssignee(
			@Parameter(description = "Delivery order id") @PathVariable Long id,
			@Valid @RequestBody UpdateDeliveryOrderAssigneeModel updateDeliveryOrderAssigneeModel) throws BusinessException {
		return ordersService.updateDeliveryOrderAssignee(id, updateDeliveryOrderAssigneeModel);
	}
}
