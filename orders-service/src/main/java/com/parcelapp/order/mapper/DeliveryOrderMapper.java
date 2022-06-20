package com.parcelapp.order.mapper;

import com.parcelapp.order.entity.DeliveryMovement;
import com.parcelapp.order.entity.DeliveryOrder;
import com.parcelapp.order.model.AddressModel;
import com.parcelapp.order.model.CreateDeliveryMovementModel;
import com.parcelapp.order.model.CreateDeliveryOrderModel;
import com.parcelapp.order.model.DeliveryMovementModel;
import com.parcelapp.order.model.DeliveryOrderModel;
import com.parcelapp.order.model.DetailedDeliveryOrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryOrderMapper {
	DeliveryOrder mapToDeliveryOrder(CreateDeliveryOrderModel createDeliveryOrderModel);

	DeliveryOrderModel mapToDeliveryOrderModel(DeliveryOrder deliveryOrder);

	DetailedDeliveryOrderModel mapToDetailedDeliveryOrderModel(DeliveryOrder deliveryOrder);

	DeliveryOrder.Address mapToAddress(AddressModel addressModel);

	DeliveryMovement mapToDeliveryMovement(CreateDeliveryMovementModel deliveryMovementModel);

	DeliveryMovementModel mapToDeliveryMovementModel(DeliveryMovement deliveryMovement);
}
