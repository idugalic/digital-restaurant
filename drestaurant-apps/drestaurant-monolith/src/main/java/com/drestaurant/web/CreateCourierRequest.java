package com.drestaurant.web;

import com.drestaurant.common.domain.model.PersonName;

/**
 * A request for creating a Courier
 *
 * @author: idugalic
 */
public class CreateCourierRequest {

	private String firstName;
	private String lastName;
	private Integer maxNumberOfActiveOrders;

	public CreateCourierRequest(PersonName name, Integer maxNumberOfActiveOrders) {
		this.maxNumberOfActiveOrders = maxNumberOfActiveOrders;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public CreateCourierRequest() {
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

	public Integer getMaxNumberOfActiveOrders() {
		return maxNumberOfActiveOrders;
	}

	public void setMaxNumberOfActiveOrders(Integer maxNumberOfActiveOrders) {
		this.maxNumberOfActiveOrders = maxNumberOfActiveOrders;
	}

}
