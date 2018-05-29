package com.drestaurant.query.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class RestaurantEntity {

	@Id
	private String id;
	@Version
	private Long version;
	private Long aggregateVersion;
	private String name;
	@Embedded
	private RestaurantMenuEmbedable menu;

	public RestaurantEntity(String id, Long aggregateVersion, String name, RestaurantMenuEmbedable menu) {
		this.id = id;
		this.aggregateVersion = aggregateVersion;
		this.name = name;
		this.menu = menu;
	}

	public RestaurantEntity() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RestaurantMenuEmbedable getMenu() {
		return menu;
	}

	public void setMenu(RestaurantMenuEmbedable menu) {
		this.menu = menu;
	}

}
