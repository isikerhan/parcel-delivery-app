package com.parcelapp.courier.model;

import com.parcelapp.courier.entity.Courier;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCourierModel {
	@NotNull(message = "registration_info is mandatory")
	private CreateUserModel registrationInfo;
	@NotNull(message = "type is mandatory")
	private Courier.CourierType type;
}
