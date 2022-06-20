package com.parcelapp.courier.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCourierResponseModel {
	private UserModel user;
	private CourierModel courier;
}
