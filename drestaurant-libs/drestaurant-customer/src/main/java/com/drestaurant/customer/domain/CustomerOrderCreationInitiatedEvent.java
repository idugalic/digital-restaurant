package com.drestaurant.customer.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;

/**
 *
 * @author: idugalic
 * Date: 5/19/18
 * Time: 10:07 AM
 */
class CustomerOrderCreationInitiatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	
	private Money orderTotal;
	private String customerId;

	public CustomerOrderCreationInitiatedEvent(Money orderTotal, String customerId, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.orderTotal = orderTotal;
		this.customerId = customerId;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}

	public String getCustomerId() {
		return customerId;
	}


}
