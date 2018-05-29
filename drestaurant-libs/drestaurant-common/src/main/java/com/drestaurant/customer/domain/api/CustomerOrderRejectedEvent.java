package com.drestaurant.customer.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class CustomerOrderRejectedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;


	public CustomerOrderRejectedEvent(String orderId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
	
	}

}
