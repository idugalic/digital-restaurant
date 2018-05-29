package com.drestaurant.restaurant.domain.api;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;

/**
 *
 * A command for creating a Restaurant
 *
 * @author: idugalic 
 * Date: 5/13/18 
 * Time: 15:29 PM
 */
public class CreateRestaurantCommand extends AuditableAbstractCommand {

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	@NotNull
	private String name;
	@Valid
	private RestaurantMenu menu;

	public CreateRestaurantCommand(String name, RestaurantMenu menu, String targetAggregateIdentifier, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.name = name;
		this.menu = menu;
	}

	public CreateRestaurantCommand(String name, RestaurantMenu menu, AuditEntry auditEntry) {
		this(name, menu, UUID.randomUUID().toString(), auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public String getName() {
		return name;
	}

	public RestaurantMenu getMenu() {
		return menu;
	}
	
	

}
