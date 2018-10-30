package com.drestaurant.common.domain.event

import com.drestaurant.common.domain.model.AuditEntry

import java.io.Serializable

abstract class AuditableAbstractEvent(open val aggregateIdentifier: String, open val auditEntry: AuditEntry) : Serializable
