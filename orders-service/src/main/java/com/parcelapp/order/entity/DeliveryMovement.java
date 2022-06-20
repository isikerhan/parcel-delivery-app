package com.parcelapp.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DELIVERY_MOVEMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class DeliveryMovement {
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

	@Embedded
	private Location location;

	private String description;

	private LocalDateTime movementDate;

	@ManyToOne
	@JoinColumn(name = "delivery_order_id")
	@ToString.Exclude
	private DeliveryOrder deliveryOrder;

	@Embeddable
	@Data
	public static class Location {
		private String locationDescription;
		private Double latitude;
		private Double longitude;
		private String district;
		private String city;
		private String countryCode;
	}
}
