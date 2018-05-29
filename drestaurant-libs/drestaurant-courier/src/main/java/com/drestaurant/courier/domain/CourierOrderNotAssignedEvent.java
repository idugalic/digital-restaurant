package com.drestaurant.courier.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

class CourierOrderNotAssignedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	public CourierOrderNotAssignedEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	}

}
