package com.drestaurant.order.domain.model;

import com.drestaurant.common.domain.model.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.validation.Valid;
import javax.validation.constraints.*;

import java.util.List;

/**
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 3:10 PM
 */
public class OrderInfo {

	@NotNull
	@Valid
	private List<OrderLineItem> lineItems;
    @NotNull
	private String restaurantId;
    @NotNull
	private String consumerId;

	public OrderInfo(String consumerId, String restaurantId, List<OrderLineItem> lineItems) {
		this.consumerId = consumerId;
		this.restaurantId = restaurantId;
		this.lineItems = lineItems;
	}

	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getConsumerId() {
		return consumerId;
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
