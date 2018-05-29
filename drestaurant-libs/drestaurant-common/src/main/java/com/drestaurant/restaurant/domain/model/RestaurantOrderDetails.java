package com.drestaurant.restaurant.domain.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RestaurantOrderDetails {
	
	private List<RestaurantOrderLineItem> lineItems;

	public RestaurantOrderDetails(List<RestaurantOrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public List<RestaurantOrderLineItem> getLineItems() {
		return Collections.unmodifiableList(lineItems);
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
