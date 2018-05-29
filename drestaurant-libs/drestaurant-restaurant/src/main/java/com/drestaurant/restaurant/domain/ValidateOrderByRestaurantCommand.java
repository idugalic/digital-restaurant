package com.drestaurant.restaurant.domain;

import java.util.List;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;

/**
 * 
 * @author idugalic
 *
 */
class ValidateOrderByRestaurantCommand extends AuditableAbstractCommand {

	private String restaurantId;
	private String orderId;
	private List<RestaurantOrderLineItem> lineItems;

	public ValidateOrderByRestaurantCommand(String orderId, String restaurantId, List<RestaurantOrderLineItem> lineItems, AuditEntry auditEntry) {
		super(auditEntry);
		this.restaurantId = restaurantId;
		this.orderId = orderId;
		this.lineItems = lineItems;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getOrderId() {
		return orderId;
	}

	public List<RestaurantOrderLineItem> getLineItems() {
		return lineItems;
	}
	
	
	
}
