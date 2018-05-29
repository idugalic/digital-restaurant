package com.drestaurant.restaurant.domain.api;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;

/**
 * @author: idugalic
 * Date: 5/18/18
 * Time: 17:24 PM
 */
public class CreateRestaurantOrderCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	@NotNull
	@Valid
	private RestaurantOrderDetails orderDetails;
	private String restaurantId;

	public CreateRestaurantOrderCommand(String targetAggregateIdentifier, RestaurantOrderDetails orderDetails, String restaurantId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.orderDetails = orderDetails;
		this.restaurantId = restaurantId;
	}

	public CreateRestaurantOrderCommand(RestaurantOrderDetails orderDetails, String restaurantId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), orderDetails, restaurantId, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public RestaurantOrderDetails getOrderDetails() {
		return orderDetails;
	}

	public String getRestaurantId() {
		return restaurantId;
	}


}
