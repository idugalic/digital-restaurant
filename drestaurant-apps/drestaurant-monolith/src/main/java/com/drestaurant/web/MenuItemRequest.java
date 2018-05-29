package com.drestaurant.web;

import java.math.BigDecimal;

public class MenuItemRequest {
	private String id;
	private String name;
	private BigDecimal price;

	public MenuItemRequest(String id, String name, BigDecimal price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public MenuItemRequest() {
	}


	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

}
