package com.parcelapp.order.model;

import com.parcelapp.order.entity.DeliveryOrder.DeliveryOrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryOrderModel {
	private Long id;
	private AddressModel destination;
	private BigDecimal price;
	private DeliveryOrderStatus status;
	private Long assignedCourierId;
}
