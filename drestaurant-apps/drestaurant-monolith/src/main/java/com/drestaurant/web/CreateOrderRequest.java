package com.drestaurant.web;

import java.util.List;

/**
 *
 * A request for creating a Restaurant
 *
 * @author: idugalic
 */
public class CreateOrderRequest {

	private String customerId;
	private String restaurantId;
	private List<OrderItemRequest> orderItems;

	public CreateOrderRequest(List<OrderItemRequest> orderItems) {

		this.orderItems = orderItems;
	}

	public CreateOrderRequest() {
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public List<OrderItemRequest> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemRequest> orderItems) {
		this.orderItems = orderItems;
	}

}
