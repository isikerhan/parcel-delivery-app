package com.parcelapp.order.model;

import com.parcelapp.order.entity.Parcel;
import lombok.Data;

@Data
public class ParcelModel {
	private Long id;
	private Parcel.ParcelType type;
	private Double weight;
}
