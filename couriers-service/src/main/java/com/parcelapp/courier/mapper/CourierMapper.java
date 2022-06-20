package com.parcelapp.courier.mapper;

import com.parcelapp.courier.entity.Courier;
import com.parcelapp.courier.model.CourierModel;
import com.parcelapp.courier.model.CreateCourierModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourierMapper {

	Courier mapToCourier(CreateCourierModel createCourierModel);

	CourierModel mapToCourierModel(Courier courier);

}
