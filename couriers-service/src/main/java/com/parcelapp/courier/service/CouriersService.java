package com.parcelapp.courier.service;

import com.parcelapp.courier.client.AuthClient;
import com.parcelapp.courier.entity.Courier;
import com.parcelapp.courier.exception.CourierNotFoundException;
import com.parcelapp.courier.mapper.CourierMapper;
import com.parcelapp.courier.model.AddMemberToGroupRequest;
import com.parcelapp.courier.model.CourierModel;
import com.parcelapp.courier.model.CreateCourierModel;
import com.parcelapp.courier.model.CreateCourierResponseModel;
import com.parcelapp.courier.model.CreateUserModel;
import com.parcelapp.courier.model.UserModel;
import com.parcelapp.courier.repository.CourierRepository;
import com.parcelapp.courier.spec.CourierFilterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.parcelapp.courier.model.AddMemberToGroupRequest.UserGroup.COURIER;

@Service
@RequiredArgsConstructor
@Transactional
public class CouriersService {

	private final CourierRepository courierRepository;
	private final AuthClient authClient;
	private final CourierMapper mapper;

	public Page<CourierModel> getCouriers(Courier.CourierType type, Courier.CourierStatus status, Pageable pageable) {
		CourierFilterSpecification specification = new CourierFilterSpecification(type, status);
		return courierRepository.findAll(specification, pageable)
				.map(mapper::mapToCourierModel);
	}

	public CourierModel getCourier(Long id) throws CourierNotFoundException {
		return courierRepository.findById(id)
				.map(mapper::mapToCourierModel)
				.orElseThrow(CourierNotFoundException::new);
	}

	public CourierModel getOwnCourier() throws CourierNotFoundException {
		return courierRepository.findOwnCourier()
				.map(mapper::mapToCourierModel)
				.orElseThrow(CourierNotFoundException::new);
	}

	public CreateCourierResponseModel createCourier(CreateCourierModel createCourierModel) {
		final UserModel userModel = createCourierUser(createCourierModel.getRegistrationInfo());

		final Courier courier = mapper.mapToCourier(createCourierModel).withUserId(userModel.getId());
		final Courier saved = courierRepository.save(courier);

		final CourierModel courierModel = mapper.mapToCourierModel(saved);
		return new CreateCourierResponseModel(userModel, courierModel);
	}

	private UserModel createCourierUser(CreateUserModel createUserModel) {
		final UserModel user = authClient.createUser(createUserModel);
		final String userId = user.getId();

		authClient.addMemberToGroup(userId, new AddMemberToGroupRequest(COURIER));

		return user;
	}
}
