package com.drestaurant.query.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable @Access(AccessType.FIELD) public class MenuItemEmbedable {

	private String menuId;
	private String name;
	private BigDecimal price;

	public MenuItemEmbedable() {
	}

	public MenuItemEmbedable(String menuId, String name, BigDecimal price) {
		this.menuId = menuId;
		this.name = name;
		this.price = price;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
