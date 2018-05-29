package com.drestaurant.web;

import java.math.BigDecimal;

public class OrderItemRequest {
	private String id;
	private String name;
	private BigDecimal price;
	private Integer quantity;

	public OrderItemRequest(String id, String name, BigDecimal price, Integer quantity) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public OrderItemRequest() {
	}


	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	

}
