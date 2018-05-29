package com.drestaurant.customer.domain.api;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;

/**
 * @author: idugalic
 * Date: 5/19/18
 * Time: 09:24 AM
 */
public class CreateCustomerOrderCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	@NotNull
	@Valid
	private Money orderTotal;
	private String customerId;

	public CreateCustomerOrderCommand(String orderId, Money orderTotal, String customerId, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = orderId;
		this.orderTotal = orderTotal;
		this.customerId = customerId;
	}

	public CreateCustomerOrderCommand(Money orderTotal, String customerId, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), orderTotal, customerId, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}

	public String getCustomerId() {
		return customerId;
	}

}
