package com.drestaurant.restaurant.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

class RestaurantNotFoundForOrderEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private String restaurantId;
	private String orderId;

	public RestaurantNotFoundForOrderEvent(String restaurantId, String orderId, AuditEntry auditEntry) {
		super(restaurantId, auditEntry);
		this.restaurantId = restaurantId;
		this.orderId = orderId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getOrderId() {
		return orderId;
	}

}
