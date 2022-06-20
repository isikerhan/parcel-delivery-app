package com.parcelapp.order.service;

import com.parcelapp.order.client.CouriersClient;
import com.parcelapp.order.exception.CourierNotFoundException;
import com.parcelapp.order.model.CourierModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouriersService {

	private final CouriersClient couriersClient;

	public CourierModel getCourier(Long id) throws CourierNotFoundException {
		return couriersClient.getCourier(id).orElseThrow(CourierNotFoundException::new);
	}

	public CourierModel getOwnCourier() throws CourierNotFoundException {
		return couriersClient.getOwnCourier().orElseThrow(CourierNotFoundException::new);
	}
}
