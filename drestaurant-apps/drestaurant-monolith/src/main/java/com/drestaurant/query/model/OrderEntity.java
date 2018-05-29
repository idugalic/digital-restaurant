package com.drestaurant.query.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.drestaurant.order.domain.model.OrderState;

@Entity
public class OrderEntity {
	@Id
	private String id;

	@Version
	private Long version;

	private Long aggregateVersion;

	@ElementCollection
	private List<OrderItemEmbedable> lineItems;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RESTAURANT_ID")
	private RestaurantEntity restaurant;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMER_ID")
	private CustomerEntity customer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COURIER_ID")
	private CourierEntity courier;

	@Enumerated(EnumType.STRING)
	private OrderState state;

	public OrderEntity(String id, Long aggregateVersion, List<OrderItemEmbedable> lineItems) {
		this.id = id;
		this.aggregateVersion = aggregateVersion;
		this.lineItems = lineItems;
		this.state = OrderState.CREATE_PENDING;
	}

	public OrderEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getAggregateVersion() {
		return aggregateVersion;
	}

	public void setAggregateVersion(Long aggregateVersion) {
		this.aggregateVersion = aggregateVersion;
	}

	public List<OrderItemEmbedable> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<OrderItemEmbedable> lineItems) {
		this.lineItems = lineItems;
	}

	public RestaurantEntity getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(RestaurantEntity restaurant) {
		this.restaurant = restaurant;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}
}
