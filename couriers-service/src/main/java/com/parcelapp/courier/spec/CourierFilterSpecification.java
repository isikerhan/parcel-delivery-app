package com.parcelapp.courier.spec;

import com.parcelapp.courier.entity.Courier;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@EqualsAndHashCode
public class CourierFilterSpecification implements Specification<Courier> {

	private final Courier.CourierType type;
	private final Courier.CourierStatus status;

	@Override
	public Predicate toPredicate(@NonNull Root<Courier> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();

		if (Objects.nonNull(status)) {
			predicates.add(criteriaBuilder.equal(root.get("status"), status));
		}

		if (Objects.nonNull(type)) {
			predicates.add(criteriaBuilder.equal(root.get("type"), type));
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
}
