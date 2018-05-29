package com.drestaurant.courier.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class CourierOrderCreatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;


	public CourierOrderCreatedEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	
	}

}
