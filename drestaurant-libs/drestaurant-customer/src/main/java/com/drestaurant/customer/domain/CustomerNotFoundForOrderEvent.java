package com.drestaurant.customer.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;

class CustomerNotFoundForOrderEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private String customerId;
	private String orderId;
	private Money orderTotal;

	public CustomerNotFoundForOrderEvent(String customerId, String orderId, Money orderTotal, AuditEntry auditEntry) {
		super(customerId, auditEntry);
		this.customerId = customerId;
		this.orderId = orderId;
		this.orderTotal = orderTotal;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}

}
