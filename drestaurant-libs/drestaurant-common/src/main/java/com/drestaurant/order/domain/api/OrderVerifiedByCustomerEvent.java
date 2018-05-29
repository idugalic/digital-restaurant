package com.drestaurant.order.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

public class OrderVerifiedByCustomerEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	private String customerId;


	public OrderVerifiedByCustomerEvent(String orderId, String customerId, AuditEntry auditEntry) {
		super(orderId, auditEntry);
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}
	

}
