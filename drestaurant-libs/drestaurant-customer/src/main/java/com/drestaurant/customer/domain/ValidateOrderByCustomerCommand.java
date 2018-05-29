package com.drestaurant.customer.domain;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;

/**
 * 
 * @author idugalic
 *
 */
class ValidateOrderByCustomerCommand extends AuditableAbstractCommand {

	private String customerId;
	private String orderId;
	private Money orderTotal;

	public ValidateOrderByCustomerCommand(String orderId, String customerId, Money orderTotal, AuditEntry auditEntry) {
		super(auditEntry);
		this.customerId = customerId;
		this.orderId = orderId;
		this.orderTotal = orderTotal;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}
	
}
