package com.parcelapp.order.repository;

import com.parcelapp.order.entity.DeliveryMovement;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryMovementRepository extends PagingAndSortingRepository<DeliveryMovement, Long> {
}
