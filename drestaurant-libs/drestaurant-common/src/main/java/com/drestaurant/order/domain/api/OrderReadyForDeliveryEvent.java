package com.drestaurant.order.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class OrderReadyForDeliveryEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;


	public OrderReadyForDeliveryEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	
	}

}
