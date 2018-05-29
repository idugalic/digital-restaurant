package com.drestaurant.common.domain.command;

import com.drestaurant.common.domain.model.AuditEntry;

/**
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 4:03 PM
 */
public class AuditableAbstractCommand {

	private AuditEntry auditEntry;

	public AuditableAbstractCommand(AuditEntry auditEntry) {
		this.auditEntry = auditEntry;
	}

	public AuditEntry getAuditEntry() {
		return auditEntry;
	}
}
