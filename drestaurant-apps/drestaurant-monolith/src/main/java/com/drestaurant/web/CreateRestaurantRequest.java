package com.drestaurant.web;

import java.util.List;

/**
 *
 * A request for creating a Restaurant
 *
 * @author: idugalic
 */
public class CreateRestaurantRequest {

	private String name;
	private List<MenuItemRequest> menuItems;

	public CreateRestaurantRequest(String name, List<MenuItemRequest> menuItems) {

		this.name = name;
		this.menuItems = menuItems;
	}

	public CreateRestaurantRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MenuItemRequest> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItemRequest> menuItems) {
		this.menuItems = menuItems;
	}

}
