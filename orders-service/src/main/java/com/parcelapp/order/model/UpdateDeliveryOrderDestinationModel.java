package com.parcelapp.order.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateDeliveryOrderDestinationModel {
	@NotNull(message = "destination is mandatory")
	private AddressModel destination;
}
