package com.parcelapp.order.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetailedDeliveryOrderModel extends DeliveryOrderModel {
	private List<ParcelModel> parcels;
	private List<DeliveryMovementModel> movements;
	private Long assignedCourierId;
}
