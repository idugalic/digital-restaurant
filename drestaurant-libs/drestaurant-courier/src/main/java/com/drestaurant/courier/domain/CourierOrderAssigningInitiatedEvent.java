package com.drestaurant.courier.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 *
 * @author: idugalic
 * Date: 5/19/18
 * Time: 10:07 AM
 */
class CourierOrderAssigningInitiatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	
	private String courierId;

	public CourierOrderAssigningInitiatedEvent(String courierId, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.courierId = courierId;
	}

	public String getCourierId() {
		return courierId;
	}


}
