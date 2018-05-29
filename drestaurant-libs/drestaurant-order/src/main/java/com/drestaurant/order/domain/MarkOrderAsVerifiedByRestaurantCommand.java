package com.drestaurant.order.domain;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * @author: idugalic
 */
class MarkOrderAsVerifiedByRestaurantCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	private String restaurantId;

	public MarkOrderAsVerifiedByRestaurantCommand(String targetAggregateIdentifier, String restaurantId,  AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.restaurantId = restaurantId;
	}

	public MarkOrderAsVerifiedByRestaurantCommand(String restaurantId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), restaurantId,  auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

}
