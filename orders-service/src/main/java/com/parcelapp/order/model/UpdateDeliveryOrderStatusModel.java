package com.parcelapp.order.model;

import com.parcelapp.order.entity.DeliveryOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateDeliveryOrderStatusModel {
	@NotNull(message = "status is mandatory")
	private DeliveryOrder.DeliveryOrderStatus status;
}
