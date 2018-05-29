package com.drestaurant.restaurant.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RestaurantMenu {

	@NotNull
	@NotEmpty
	@Valid
	private List<MenuItem> menuItems;
	private String menuVersion;

	public RestaurantMenu(List<MenuItem> menuItems, String menuVersion) {
		this.menuItems = menuItems;
		this.menuVersion = menuVersion;
	}

	public List<MenuItem> getMenuItems() {
		return Collections.unmodifiableList(this.menuItems);
	}
	
	public List<MenuItem> addMenuItem(MenuItem menuItem){
		if (this.menuItems == null) this.menuItems = new ArrayList<MenuItem>();
		this.menuItems.add(menuItem);
		return Collections.unmodifiableList(this.menuItems);
	}
	
	public List<MenuItem> removeMenuItem(MenuItem menuItem){
		if (this.menuItems == null) this.menuItems = new ArrayList<MenuItem>();
		this.menuItems.remove(menuItem);
		return Collections.unmodifiableList(this.menuItems);
	}

	public String getMenuVersion() {
		return this.menuVersion;
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
