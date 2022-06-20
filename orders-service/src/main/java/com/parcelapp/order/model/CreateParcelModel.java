package com.parcelapp.order.model;

import com.parcelapp.order.entity.Parcel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateParcelModel {
	@NotNull(message = "type is mandatory")
	private Parcel.ParcelType type;
	@NotNull(message = "weight is mandatory")
	private Double weight;
}
