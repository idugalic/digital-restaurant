package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName

/**
 * An event, noting that customer has been created
 */
class CustomerCreatedEvent(val name: PersonName, val orderLimit: Money, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that customer order has been created
 */
class CustomerOrderCreatedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

/**
 * An event, noting that customer order has been delivered
 */
class CustomerOrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

/**
 * An event, noting that customer order has been rejected
 */
class CustomerOrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
