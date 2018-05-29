package com.drestaurant.restaurant.domain;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * @author: idugalic
 * Date: 5/18/18
 * Time: 17:24 PM
 */
class MarkRestaurantOrderAsCreatedCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	public MarkRestaurantOrderAsCreatedCommand(String targetAggregateIdentifier, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
	}

	public MarkRestaurantOrderAsCreatedCommand(AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

}
