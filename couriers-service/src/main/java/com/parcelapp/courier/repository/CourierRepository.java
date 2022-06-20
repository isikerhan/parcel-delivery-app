package com.parcelapp.courier.repository;

import com.parcelapp.courier.entity.Courier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends PagingAndSortingRepository<Courier, Long>, JpaSpecificationExecutor<Courier> {
	@Query("select c from Courier c where c.userId = ?#{ principal?.getClaim('user_id') }")
	Optional<Courier> findOwnCourier();
}
