package com.parcelapp.order.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreateDeliveryOrderModel {
	@NotNull(message = "parcels is mandatory")
	@NotEmpty(message = "parcels must have at least 1 item")
	@Size(max = 5, message = "parcels can have 5 items at maximum")
	private List<CreateParcelModel> parcels;
	@NotNull(message = "destination is mandatory")
	private AddressModel destination;
}
