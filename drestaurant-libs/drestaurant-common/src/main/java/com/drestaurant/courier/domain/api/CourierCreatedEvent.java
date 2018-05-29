package com.drestaurant.courier.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.PersonName;

public class CourierCreatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;

	private PersonName name;
	private Integer maxNumberOfActiveOrders;

	public CourierCreatedEvent(PersonName name, Integer maxNumberOfActiveOrders, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.name = name;
		this.maxNumberOfActiveOrders = maxNumberOfActiveOrders;
	}

	public PersonName getName() {
		return name;
	}

	public Integer getMaxNumberOfActiveOrders() {
		return maxNumberOfActiveOrders;
	}
	
	

}
