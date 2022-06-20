package com.parcelapp.order.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AddressModel {
	@NotBlank(message = "address_line1 is mandatory")
	private String addressLine1;
	private String addressLine2;
	@NotBlank(message = "district is mandatory")
	private String district;
	@NotBlank(message = "city is mandatory")
	private String city;
	@NotBlank(message = "country_code is mandatory")
	@Pattern(regexp = "[A-Z]{2}",
			message = "country_code must consist of 2 uppercase letters")
	private String countryCode;
	@NotBlank(message = "zip_code is mandatory")
	private String zipCode;
	@NotBlank(message = "receiver_name is mandatory")
	private String receiverName;
	@NotBlank(message = "receiver_surname is mandatory")
	private String receiverSurname;
	@NotBlank(message = "receiver_phone is mandatory")
	private String receiverPhone;
}
