package com.parcelapp.order.service;

import com.parcelapp.order.entity.DeliveryMovement;
import com.parcelapp.order.entity.DeliveryOrder;
import com.parcelapp.order.entity.Parcel;
import com.parcelapp.order.exception.BusinessException;
import com.parcelapp.order.exception.CourierNotFoundException;
import com.parcelapp.order.exception.IllegalOrderStatusException;
import com.parcelapp.order.exception.OrderNotFoundException;
import com.parcelapp.order.mapper.DeliveryOrderMapper;
import com.parcelapp.order.model.CourierModel;
import com.parcelapp.order.model.CreateDeliveryMovementModel;
import com.parcelapp.order.model.CreateDeliveryOrderModel;
import com.parcelapp.order.model.CreateParcelModel;
import com.parcelapp.order.model.DeliveryMovementModel;
import com.parcelapp.order.model.DeliveryOrderModel;
import com.parcelapp.order.model.DetailedDeliveryOrderModel;
import com.parcelapp.order.model.UpdateDeliveryOrderAssigneeModel;
import com.parcelapp.order.model.UpdateDeliveryOrderDestinationModel;
import com.parcelapp.order.model.UpdateDeliveryOrderStatusModel;
import com.parcelapp.order.repository.DeliveryMovementRepository;
import com.parcelapp.order.repository.DeliveryOrderRepository;
import com.parcelapp.order.repository.ParcelRepository;
import com.parcelapp.order.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.parcelapp.order.entity.DeliveryOrder.DeliveryOrderStatus.CANCELLED;
import static com.parcelapp.order.entity.DeliveryOrder.DeliveryOrderStatus.CONFIRMED;
import static com.parcelapp.order.entity.DeliveryOrder.DeliveryOrderStatus.CREATED;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdersService {

	private static final List<DeliveryOrder.DeliveryOrderStatus> DESTINATION_UPDATABLE_ORDER_STATUSES = List.of(
			CREATED, CONFIRMED
	);
	private static final List<DeliveryOrder.DeliveryOrderStatus> CANCELLABLE_ORDER_STATUSES = List.of(CREATED);

	private final DeliveryOrderRepository deliveryOrderRepository;
	private final ParcelRepository parcelRepository;
	private final DeliveryMovementRepository deliveryMovementRepository;
	private final CouriersService couriersService;
	private final DeliveryOrderMapper mapper;

	public DeliveryOrderModel createDeliveryOrder(CreateDeliveryOrderModel createDeliveryOrderModel) {
		final DeliveryOrder deliveryOrder = mapper.mapToDeliveryOrder(createDeliveryOrderModel)
				.withStatus(CREATED)
				.withPrice(calculatePrice(createDeliveryOrderModel));
		final DeliveryOrder saved = deliveryOrderRepository.save(deliveryOrder);

		final List<Parcel> parcels = deliveryOrder.getParcels().stream()
				.map(parcel -> parcel.withDeliveryOrder(deliveryOrder))
				.collect(Collectors.toList());
		parcelRepository.saveAll(parcels);

		return mapper.mapToDeliveryOrderModel(saved);
	}

	static BigDecimal calculatePrice(CreateDeliveryOrderModel createDeliveryOrderModel) {
		return createDeliveryOrderModel.getParcels()
				.stream()
				.map(OrdersService::calculateParcelPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static BigDecimal calculateParcelPrice(CreateParcelModel parcelModel) {
		double multiplier;
		switch (parcelModel.getType()) {
			case SMALL:
				multiplier = 1.1;
				break;
			case MEDIUM:
				multiplier = 1.2;
				break;
			case LARGE:
				multiplier = 1.4;
				break;
			default:
				multiplier = 1.0;
				break;
		}
		final double weight = parcelModel.getWeight();
		final double price = weight * 10.0 * multiplier;
		return new BigDecimal(price);
	}

	public Page<DeliveryOrderModel> getDeliveryOrders(Pageable pageable) {
		return deliveryOrderRepository.findAll(pageable)
				.map(mapper::mapToDeliveryOrderModel);
	}

	public Page<DeliveryOrderModel> getOwnDeliveryOrders(Pageable pageable) {
		return deliveryOrderRepository.findOwnOrders(pageable)
				.map(mapper::mapToDeliveryOrderModel);
	}

	public DeliveryMovementModel createDeliveryMovement(Long orderId, CreateDeliveryMovementModel createDeliveryMovementModel) throws OrderNotFoundException {
		DeliveryOrder order = deliveryOrderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
		DeliveryMovement movement = mapper.mapToDeliveryMovement(createDeliveryMovementModel)
				.withDeliveryOrder(order);

		DeliveryMovement saved = deliveryMovementRepository.save(movement);
		return mapper.mapToDeliveryMovementModel(saved);
	}

	public DeliveryOrderModel updateDeliveryOrderDestination(Long id, UpdateDeliveryOrderDestinationModel updateDeliveryOrderModel) throws BusinessException {
		DeliveryOrder order = deliveryOrderRepository.findOwnOrderById(id).orElseThrow(OrderNotFoundException::new);
		if (!DESTINATION_UPDATABLE_ORDER_STATUSES.contains(order.getStatus())) {
			throw new IllegalOrderStatusException("Destination of the delivery cannot be updated " +
					"because order status is not appropriate!");
		}
		order = order.withDestination(mapper.mapToAddress(updateDeliveryOrderModel.getDestination()));

		DeliveryOrder saved = deliveryOrderRepository.save(order);
		return mapper.mapToDeliveryOrderModel(saved);
	}

	public DeliveryOrderModel updateDeliveryOrderStatus(Long id, UpdateDeliveryOrderStatusModel updateDeliveryOrderStatusModel,
														JwtAuthenticationToken jwtAuthenticationToken) throws BusinessException {
		final DeliveryOrder.DeliveryOrderStatus status = updateDeliveryOrderStatusModel.getStatus();
		if (!AuthUtils.hasAuthority(jwtAuthenticationToken, "SCOPE_parcel_app.admin") &&
				!AuthUtils.hasAuthority(jwtAuthenticationToken, "SCOPE_parcel_app.courier")) {
			return cancelOrder(id, status);
		} else {
			return updateDeliveryOrderStatus(id, status);
		}
	}

	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin') or hasAuthority('SCOPE_parcel_app.courier')")
	private DeliveryOrderModel updateDeliveryOrderStatus(Long id, DeliveryOrder.DeliveryOrderStatus status) throws OrderNotFoundException {
		DeliveryOrder order = deliveryOrderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
		order = order.withStatus(status);

		DeliveryOrder saved = deliveryOrderRepository.save(order);
		return mapper.mapToDeliveryOrderModel(saved);
	}

	private DeliveryOrderModel cancelOrder(Long id, DeliveryOrder.DeliveryOrderStatus status) throws BusinessException {
		// only cancellation is allowed
		if (status != CANCELLED) {
			throw new IllegalOrderStatusException("Order status cannot be updated to " +
					status.name() + "!");
		}
		DeliveryOrder order = deliveryOrderRepository.findOwnOrderById(id).orElseThrow(OrderNotFoundException::new);
		if (!CANCELLABLE_ORDER_STATUSES.contains(order.getStatus())) {
			throw new IllegalOrderStatusException("Status of the delivery cannot be updated " +
					"because order status is not appropriate!");
		}
		order = order.withStatus(status);
		DeliveryOrder saved = deliveryOrderRepository.save(order);
		return mapper.mapToDeliveryOrderModel(saved);
	}

	public DetailedDeliveryOrderModel getDeliveryOrder(Long id, JwtAuthenticationToken jwtAuthenticationToken) throws OrderNotFoundException {
		DeliveryOrder deliveryOrder;
		if (!AuthUtils.hasAuthority(jwtAuthenticationToken, "SCOPE_parcel_app.admin")) {
			deliveryOrder = getOwnDeliveryOrder(id);
		} else {
			deliveryOrder = getAnyDeliveryOrder(id);
		}

		return mapper.mapToDetailedDeliveryOrderModel(deliveryOrder);
	}

	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	private DeliveryOrder getAnyDeliveryOrder(Long id) throws OrderNotFoundException {
		return deliveryOrderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
	}

	private DeliveryOrder getOwnDeliveryOrder(Long id) throws OrderNotFoundException {
		return deliveryOrderRepository.findOwnOrderById(id).orElseThrow(OrderNotFoundException::new);
	}

	public DeliveryOrderModel updateDeliveryOrderAssignee(Long id, UpdateDeliveryOrderAssigneeModel updateDeliveryOrderAssigneeModel) throws CourierNotFoundException, OrderNotFoundException {
		Long courierId = updateDeliveryOrderAssigneeModel.getAssignedCourierId();

		DeliveryOrder order = deliveryOrderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
		// make sure courier exists
		CourierModel courier = couriersService.getCourier(courierId);

		order = order.withAssignedCourierId(courier.getId());
		DeliveryOrder saved = deliveryOrderRepository.save(order);

		return mapper.mapToDetailedDeliveryOrderModel(saved);
	}

	public Page<DeliveryOrderModel> getAssignedDeliveryOrders(Pageable pageable) throws CourierNotFoundException {
		CourierModel courier = couriersService.getOwnCourier();
		return deliveryOrderRepository.findAllByAssignedCourierId(courier.getId(), pageable)
				.map(mapper::mapToDeliveryOrderModel);
	}
}
