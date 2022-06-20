package com.parcelapp.courier.model;

import com.parcelapp.courier.entity.Courier;
import lombok.Data;

@Data
public class CourierModel {
	private Long id;
	private String userId;
	private Courier.CourierType type;
	private Courier.CourierStatus status;
}
