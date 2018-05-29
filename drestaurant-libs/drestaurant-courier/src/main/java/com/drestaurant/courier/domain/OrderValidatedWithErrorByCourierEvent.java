package com.drestaurant.courier.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

class OrderValidatedWithErrorByCourierEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private String courierId;
	private String orderId;

	public OrderValidatedWithErrorByCourierEvent(String courierId, String orderId, AuditEntry auditEntry) {
		super(courierId, auditEntry);
		this.courierId = courierId;
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getCourierId() {
		return courierId;
	}

}
