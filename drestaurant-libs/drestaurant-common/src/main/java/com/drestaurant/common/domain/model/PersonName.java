package com.drestaurant.common.domain.model;

import javax.validation.constraints.NotNull;

public class PersonName {
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;

	public PersonName(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
