package com.drestaurant.common.domain.event

import com.drestaurant.common.domain.model.AuditEntry

import java.io.Serializable

/**
 *
 * @author: idugalic
 */
open class AuditableAbstractEvent(val aggregateIdentifier: String, val auditEntry: AuditEntry) : Serializable
