package com.drestaurant.courier.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry

/**
 * Internal events, scoped to 'courier' bounded context only
 */

internal class CourierNotFoundForOrderInternalEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)

internal class CourierOrderAssigningInitiatedInternalEvent(val courierId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

internal class OrderValidatedWithErrorByCourierInternalEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)

internal class OrderValidatedWithSuccessByCourierInternalEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)
