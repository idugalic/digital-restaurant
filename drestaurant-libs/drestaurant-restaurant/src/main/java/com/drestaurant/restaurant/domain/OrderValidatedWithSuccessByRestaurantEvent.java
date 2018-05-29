package com.drestaurant.restaurant.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

class OrderValidatedWithSuccessByRestaurantEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private String restaurantId;
	private String orderId;

	public OrderValidatedWithSuccessByRestaurantEvent(String restaurantId, String orderId, AuditEntry auditEntry) {
		super(restaurantId, auditEntry);
		this.restaurantId = restaurantId;
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

}
