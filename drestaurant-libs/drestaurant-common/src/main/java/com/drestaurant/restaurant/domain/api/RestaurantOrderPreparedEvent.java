package com.drestaurant.restaurant.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class RestaurantOrderPreparedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;


	public RestaurantOrderPreparedEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	
	}

}
