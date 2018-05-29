package com.drestaurant.order.domain.api;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.order.domain.model.OrderInfo;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 *
 *  A command for creating an {@link com.drestaurant.order.domain.Order}
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 12:24 PM
 */
public class CreateOrderCommand extends AuditableAbstractCommand{

	@TargetAggregateIdentifier
	private String targetAggregateIdentifier;

	@NotNull
	@Valid
	private OrderInfo orderInfo;

	public CreateOrderCommand(String targetAggregateIdentifier, OrderInfo orderInfo, AuditEntry auditEntry) {
		super(auditEntry);
		this.targetAggregateIdentifier = targetAggregateIdentifier;
		this.orderInfo = orderInfo;
	}

	public CreateOrderCommand(OrderInfo orderInfo, AuditEntry auditEntry) {
		this(UUID.randomUUID().toString(), orderInfo, auditEntry);
	}

	public String getTargetAggregateIdentifier() {
		return targetAggregateIdentifier;
	}

	public OrderInfo getOrderInfo() {
		return orderInfo;
	}
}
