package com.parcelapp.order.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateDeliveryOrderAssigneeModel {
	@NotNull(message = "assigned_courier_id is mandatory")
	private Long assignedCourierId;
}
