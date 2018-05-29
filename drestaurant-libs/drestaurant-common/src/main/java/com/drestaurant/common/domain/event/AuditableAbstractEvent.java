package com.drestaurant.common.domain.event;

import com.drestaurant.common.domain.model.AuditEntry;

import java.io.Serializable;

/**
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 3:49 PM
 */
public class AuditableAbstractEvent implements Serializable {

	private String aggregateIdentifier;
	private AuditEntry auditEntry;

	public AuditableAbstractEvent(String aggregateIdentifier, AuditEntry auditEntry) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.auditEntry = auditEntry;
	}

	public AuditEntry getAuditEntry() {
		return auditEntry;
	}

	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}

}
