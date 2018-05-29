package com.drestaurant.courier.domain.api;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * A command for creating a CourierOrder (a courier representation of an order)
 * Note that Courier order is not yet assigned to a Courier, but it is simply queued for delivery.
 * 
 * @author: idugalic
 */
public class CreateCourierOrderCommand extends AuditableAbstractCommand {

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	public CreateCourierOrderCommand(String orderId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = orderId;
	}

	public CreateCourierOrderCommand(AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

}
