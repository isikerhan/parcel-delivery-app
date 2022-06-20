package com.parcelapp.courier.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "COURIER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class Courier {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedBy
	private String createdBy;

	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedBy
	private String lastModifiedBy;

	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	private String userId;

	@Enumerated(EnumType.STRING)
	private CourierType type;

	@Enumerated(EnumType.STRING)
	private CourierStatus status = CourierStatus.AVAILABLE;

	public enum CourierType {
		WALKING_COURIER, MOTO_COURIER, CAR_COURIER, BIKE_COURIER
	}

	public enum CourierStatus {
		AVAILABLE, UNAVAILABLE
	}
}
