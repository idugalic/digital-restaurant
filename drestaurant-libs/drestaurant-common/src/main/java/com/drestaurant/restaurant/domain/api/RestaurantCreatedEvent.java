package com.drestaurant.restaurant.domain.api;

import com.drestaurant.common.domain.event.AuditableAbstractEvent;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;

public class RestaurantCreatedEvent extends AuditableAbstractEvent {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private RestaurantMenu menu;

	public RestaurantCreatedEvent(String name, RestaurantMenu menu, String aggregateIdentifier, AuditEntry auditEntry) {
		super(aggregateIdentifier, auditEntry);
		this.name = name;
		this.menu = menu;
	}

	public String getName() {
		return name;
	}

	public RestaurantMenu getMenu() {
		return menu;
	}
	
	

}
