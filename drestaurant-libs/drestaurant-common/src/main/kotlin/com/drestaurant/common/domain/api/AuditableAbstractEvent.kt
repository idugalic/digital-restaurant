package com.drestaurant.common.domain.api

import com.drestaurant.common.domain.api.model.AuditEntry

import java.io.Serializable

abstract class AuditableAbstractEvent(open val aggregateIdentifier: String, open val auditEntry: AuditEntry) : Serializable
