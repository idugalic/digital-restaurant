package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName

/**
 * An event, noting that courier has been created
 */
class CourierCreatedEvent(val name: PersonName, val maxNumberOfActiveOrders: Int, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
/**
 * An event, noting that courier order has been assigned to courier
 */
class CourierOrderAssignedEvent(orderId: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that courier order has been created
 */
class CourierOrderCreatedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that courier order has been delivered
 */
class CourierOrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)