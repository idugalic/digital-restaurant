package com.drestaurant.restaurant.domain.model;

import com.drestaurant.common.domain.model.Money;

public class MenuItem {
	private String id;
	private String name;
	private Money price;

	public MenuItem(String id, String name, Money price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public MenuItem() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

}
