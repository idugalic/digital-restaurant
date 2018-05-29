package com.drestaurant.order.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.order.domain.model.OrderDetails;

/**
 *
 * @author: idugalic
 * Date: 5/19/18
 * Time: 07:17 PM
 */
public class OrderCreationInitiatedEvent extends AuditableAbstractEvent {

private static final long serialVersionUID = 1L;
	
	private OrderDetails orderDetails;

	public OrderCreationInitiatedEvent(OrderDetails orderDetails, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.orderDetails = orderDetails;
	}

	public OrderDetails getOrderDetails() {
		return orderDetails;
	}

}
