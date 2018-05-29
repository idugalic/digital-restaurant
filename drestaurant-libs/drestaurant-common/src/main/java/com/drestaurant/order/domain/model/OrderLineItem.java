package com.drestaurant.order.domain.model;

import com.drestaurant.common.domain.model.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 2:55 PM
 */
public class OrderLineItem {

	@NotNull
	private Integer quantity;
	@NotNull
	private String menuItemId;
	@NotNull
	private String name;
	@NotNull
	@Valid
	private Money price;

	public OrderLineItem(String menuItemId, String name, Money price, Integer quantity) {
		this.menuItemId = menuItemId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	public Money deltaForChangedQuantity(int newQuantity) {
		return price.multiply(newQuantity - quantity);
	}

	public int getQuantity() {
		return quantity;
	}

	public String getMenuItemId() {
		return menuItemId;
	}

	public String getName() {
		return name;
	}

	public Money getPrice() {
		return price;
	}

	public Money getTotal() {
		return price.multiply(quantity);
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
