package com.drestaurant.restaurant.domain;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;

/**
 *
 * @author: idugalic
 * Date: 5/18/18
 * Time: 05:07 PM
 */
class RestaurantOrderCreationInitiatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	
	private RestaurantOrderDetails orderDetails;
	private String restaurantId;

	public RestaurantOrderCreationInitiatedEvent(RestaurantOrderDetails orderDetails, String restaurantId, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.orderDetails = orderDetails;
		this.restaurantId = restaurantId;
	}

	public RestaurantOrderDetails getOrderDetails() {
		return orderDetails;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

}
