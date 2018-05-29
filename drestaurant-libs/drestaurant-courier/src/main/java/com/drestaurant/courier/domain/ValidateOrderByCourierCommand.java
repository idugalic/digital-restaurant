package com.drestaurant.courier.domain;

import com.drestaurant.common.domain.command.AuditableAbstractCommand;
import com.drestaurant.common.domain.model.AuditEntry;

/**
 * 
 * @author idugalic
 *
 */
class ValidateOrderByCourierCommand extends AuditableAbstractCommand {

	private String courierId;
	private String orderId;

	public ValidateOrderByCourierCommand(String orderId, String courierId, AuditEntry auditEntry) {
		super(auditEntry);
		this.courierId = courierId;
		this.orderId = orderId;
	}

	public String getCourierId() {
		return courierId;
	}

	public String getOrderId() {
		return orderId;
	}

}
