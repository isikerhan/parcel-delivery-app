package com.parcelapp.order.repository;

import com.parcelapp.order.entity.Parcel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelRepository extends PagingAndSortingRepository<Parcel, Long> {
}
