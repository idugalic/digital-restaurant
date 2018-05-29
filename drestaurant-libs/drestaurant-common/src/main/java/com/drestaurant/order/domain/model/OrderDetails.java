package com.drestaurant.order.domain.model;

import com.drestaurant.common.domain.model.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 3:00 PM
 */
public class OrderDetails extends OrderInfo {

	private Money orderTotal;

	public OrderDetails(String consumerId, String restaurantId, List<OrderLineItem> lineItems, Money orderTotal) {
		super(consumerId, restaurantId, lineItems);
		this.orderTotal = orderTotal;
	}
	public OrderDetails(OrderInfo orderInfo, Money orderTotal) {
		super(orderInfo.getConsumerId(), orderInfo.getRestaurantId(), orderInfo.getLineItems());
		this.orderTotal = orderTotal;
	}

	public Money getOrderTotal() {
		return orderTotal;
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
