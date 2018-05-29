package com.drestaurant.order.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class OrderVerifiedByRestaurantEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	private String restaurantId;


	public OrderVerifiedByRestaurantEvent(String orderId, String restaurantId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	    this.restaurantId = restaurantId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

}
