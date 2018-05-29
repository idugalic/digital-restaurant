package com.drestaurant.courier.domain.api;

import java.util.UUID;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * A command for assigning a CourierOrder to a Courier
 * 
 * @author: idugalic
 */
public class AssignCourierOrderToCourierCommand extends AuditableAbstractCommand {

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	private String courierId;

	public AssignCourierOrderToCourierCommand(String orderId, String courierId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = orderId;
		this.courierId = courierId;
	}

	public AssignCourierOrderToCourierCommand(String courierId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), courierId, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public String getCourierId() {
		return courierId;
	}

}
