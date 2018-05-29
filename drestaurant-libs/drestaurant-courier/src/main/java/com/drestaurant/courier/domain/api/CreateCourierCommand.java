package com.drestaurant.courier.domain.api;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.PersonName;

/**
 * A command for creating a Courier
 *
 * @author: idugalic
 */
public class CreateCourierCommand extends AuditableAbstractCommand {

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	@NotNull
	@Valid
	private PersonName name;
	private Integer maxNumberOfActiveOrders;

	public CreateCourierCommand(String targetAggregateIdentifier, PersonName name, Integer maxNumberOfActiveOrders, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.maxNumberOfActiveOrders = maxNumberOfActiveOrders;
		this.name = name;
	}

	public CreateCourierCommand(PersonName name, Integer maxNumberOfActiveOrders, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), name, maxNumberOfActiveOrders, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public PersonName getName() {
		return name;
	}

	public Integer getMaxNumberOfActiveOrders() {
		return maxNumberOfActiveOrders;
	}

}
