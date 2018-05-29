package com.drestaurant.restaurant.domain.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RestaurantOrderLineItem {

	@NotNull
	private Integer quantity;
	@NotNull
	private String menuItemId;
	private String name;

	public RestaurantOrderLineItem(Integer quantity, String menuItemId, String name) {
		this.quantity = quantity;
		this.menuItemId = menuItemId;
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public String getMenuItemId() {
		return menuItemId;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
