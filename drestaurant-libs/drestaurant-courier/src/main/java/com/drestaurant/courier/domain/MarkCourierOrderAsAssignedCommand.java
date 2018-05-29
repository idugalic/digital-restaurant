package com.drestaurant.courier.domain;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * @author: idugalic
 * Date: 5/18/18
 * Time: 17:24 PM
 */
class MarkCourierOrderAsAssignedCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	private String courierId;

	public MarkCourierOrderAsAssignedCommand(String targetAggregateIdentifier, String courierId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.courierId = courierId;
	}

	public MarkCourierOrderAsAssignedCommand(String courierId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), courierId, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public String getCourierId() {
		return courierId;
	}

}
