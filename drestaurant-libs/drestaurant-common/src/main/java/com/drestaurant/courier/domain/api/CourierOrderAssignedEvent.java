package com.drestaurant.courier.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class CourierOrderAssignedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
    private String courierId;

	public CourierOrderAssignedEvent(String orderId, String courierId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
		this.courierId = courierId;
	}

	public String getCourierId() {
		return courierId;
	}
}
