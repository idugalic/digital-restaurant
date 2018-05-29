package com.drestaurant.order.domain;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * @author: idugalic
 */
class MarkOrderAsVerifiedByCustomerCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	private String customerId;

	public MarkOrderAsVerifiedByCustomerCommand(String targetAggregateIdentifier, String customerId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.customerId = customerId;
	}

	public MarkOrderAsVerifiedByCustomerCommand(String customerId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), customerId, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public String getCustomerId() {
		return customerId;
	}

}
