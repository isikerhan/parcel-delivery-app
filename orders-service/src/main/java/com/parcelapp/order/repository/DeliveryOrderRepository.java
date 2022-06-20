package com.parcelapp.order.repository;

import com.parcelapp.order.entity.DeliveryOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryOrderRepository extends PagingAndSortingRepository<DeliveryOrder, Long> {
	@Query("select o from DeliveryOrder o where o.createdBy = ?#{ principal?.getClaim('user_id') }")
	Page<DeliveryOrder> findOwnOrders(Pageable pageable);

	@Query("select o from DeliveryOrder o where o.createdBy = ?#{ principal?.getClaim('user_id') } and o.id = :id")
	Optional<DeliveryOrder> findOwnOrderById(@Param("id") Long id);

	Page<DeliveryOrder> findAllByAssignedCourierId(Long assignedCourierId, Pageable pageable);
}
