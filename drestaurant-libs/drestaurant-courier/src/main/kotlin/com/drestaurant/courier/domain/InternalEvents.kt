package com.drestaurant.courier.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry

/**
 * @author: idugalic
 */

internal class CourierNotFoundForOrderEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)

internal class CourierOrderAssigningInitiatedEvent(val courierId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

internal class OrderValidatedWithErrorByCourierEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)

internal class OrderValidatedWithSuccessByCourierEvent(val courierId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(courierId, auditEntry)
