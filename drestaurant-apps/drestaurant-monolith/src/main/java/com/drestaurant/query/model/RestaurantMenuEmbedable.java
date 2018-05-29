package com.drestaurant.query.model;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Embeddable
@Access(AccessType.FIELD)
public class RestaurantMenuEmbedable {

	@ElementCollection
	private List<MenuItemEmbedable> menuItems;
	private String menuVersion;

	public RestaurantMenuEmbedable(List<MenuItemEmbedable> menuItems, String menuVersion) {
		super();
		this.menuItems = menuItems;
		this.menuVersion = menuVersion;
	}

	public RestaurantMenuEmbedable() {

	}

	public List<MenuItemEmbedable> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItemEmbedable> menuItems) {
		this.menuItems = menuItems;
	}

	public String getMenuVersion() {
		return menuVersion;
	}

	public void setMenuVersion(String menuVersion) {
		this.menuVersion = menuVersion;
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
