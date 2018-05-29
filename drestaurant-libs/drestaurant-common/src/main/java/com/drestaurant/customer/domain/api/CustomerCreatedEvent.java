package com.drestaurant.customer.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;

public class CustomerCreatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private PersonName name;
	private Money orderLimit;

	public CustomerCreatedEvent(PersonName name, Money orderLimit, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.name = name;
		this.orderLimit = orderLimit;
	}

	public PersonName getName() {
		return name;
	}

	public Money getOrderLimit() {
		return orderLimit;
	}

}
