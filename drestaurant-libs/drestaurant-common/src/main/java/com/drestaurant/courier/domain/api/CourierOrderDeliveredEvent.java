package com.drestaurant.courier.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class CourierOrderDeliveredEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	public CourierOrderDeliveredEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	}

}
