package com.drestaurant.customer.domain.api;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;

/**
 *
 * A command for creating a Customer/Consumer
 *
 * @author: idugalic Date: 5/2/18 Time: 11:29 PM
 */
public class CreateCustomerCommand extends AuditableAbstractCommand {

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;
	@NotNull
	@Valid
	private PersonName name;
	private Money orderLimit;

	public CreateCustomerCommand(String targetAggregateIdentifier, PersonName name, Money orderLimit, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.name = name;
		this.orderLimit = orderLimit;
	}

	public CreateCustomerCommand(PersonName name, Money orderLimit, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), name, orderLimit, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public PersonName getName() {
		return name;
	}

	public Money getOrderLimit() {
		return orderLimit;
	}

}
