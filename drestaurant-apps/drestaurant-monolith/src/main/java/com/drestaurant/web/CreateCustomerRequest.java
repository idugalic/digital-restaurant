package com.drestaurant.web;

import java.math.BigDecimal;

import com.drestaurant.common.domain.model.PersonName;

/**
 *
 * A request for creating a Customer/Consumer
 *
 * @author: idugalic
 */
public class CreateCustomerRequest {

	private String firstName;
	private String lastName;
	private BigDecimal orderLimit;

	public CreateCustomerRequest(PersonName name, BigDecimal orderLimit) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.orderLimit = orderLimit;
	}

	public CreateCustomerRequest() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public BigDecimal getOrderLimit() {
		return orderLimit;
	}

	public void setOrderLimit(BigDecimal orderLimit) {
		this.orderLimit = orderLimit;
	}
}
