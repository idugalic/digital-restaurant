package com.drestaurant.query.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity public class CourierEntity {

	@Id private String id;
	@Version private Long version;
	private Long aggregateVersion;
	private String firstName;
	private String lastName;
	private Integer maxNumberOfActiveOrders;

	public CourierEntity(String id, Long aggregateVersion, String firstName, String lastName, Integer maxNumberOfActiveOrders) {
		this.id = id;
		this.aggregateVersion = aggregateVersion;
		this.firstName = firstName;
		this.lastName = lastName;
		this.maxNumberOfActiveOrders = maxNumberOfActiveOrders;
	}

	public CourierEntity() {
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
