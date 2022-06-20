package com.parcelapp.order.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DELIVERY_ORDER")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@With
public class DeliveryOrder {
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

	@OneToMany(mappedBy = "deliveryOrder")
	private List<Parcel> parcels;

	@OneToMany(mappedBy = "deliveryOrder")
	private List<DeliveryMovement> movements;

	@Embedded
	private Address destination;

	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	private DeliveryOrderStatus status;

	private Long assignedCourierId;

	@Embeddable
	@Data
	public static class Address {
		@Column(name = "address_line1")
		private String addressLine1;
		@Column(name = "address_line2")
		private String addressLine2;
		private String district;
		private String city;
		private String countryCode;
		private String zipCode;
		private String receiverName;
		private String receiverSurname;
		private String receiverPhone;
	}

	public enum DeliveryOrderStatus {
		CREATED, CONFIRMED, PICKED_UP, IN_DELIVERY, DELIVERED, CANCELLED
	}
}
