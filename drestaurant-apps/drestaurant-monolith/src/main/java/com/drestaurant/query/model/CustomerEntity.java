package com.drestaurant.query.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;

@Entity public class CustomerEntity {
	@Id private String id;
	@Version private Long version;
	private Long aggregateVersion;
	private String firstName;
	private String lastName;
	private BigDecimal orderLimit;

	public CustomerEntity() {
	}

	public CustomerEntity(String id, Long aggregateVersion, String firstName, String lastName, BigDecimal orderLimit) {
		super();
		this.id = id;
		this.aggregateVersion = aggregateVersion;
		this.firstName = firstName;
		this.lastName = lastName;
		this.orderLimit = orderLimit;
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

	public BigDecimal getOrderLimit() {
		return orderLimit;
	}

	public void setOrderLimit(BigDecimal orderLimit) {
		this.orderLimit = orderLimit;
	}

}
