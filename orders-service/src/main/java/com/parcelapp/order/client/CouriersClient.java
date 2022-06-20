package com.parcelapp.order.client;

import com.parcelapp.order.model.CourierModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "couriers-client", url = "${urls.couriers-service}", path = "/api")
public interface CouriersClient {

	@GetMapping("/couriers/{id}")
	Optional<CourierModel> getCourier(@PathVariable Long id);

	@GetMapping("/couriers/me")
	Optional<CourierModel> getOwnCourier();
}
