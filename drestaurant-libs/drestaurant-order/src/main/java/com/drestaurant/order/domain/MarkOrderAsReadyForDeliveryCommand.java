package com.drestaurant.order.domain;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * @author: idugalic
 * Date: 5/18/18
 * Time: 17:24 PM
 */
class MarkOrderAsReadyForDeliveryCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	public MarkOrderAsReadyForDeliveryCommand(String targetAggregateIdentifier, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
	}

	public MarkOrderAsReadyForDeliveryCommand(AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

}
