package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName

/**
 * An event, noting that 'customer' has been created
 */
data class CustomerCreatedEvent(val name: PersonName, val orderLimit: Money, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been created
 */
data class CustomerOrderCreatedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been delivered
 */
data class CustomerOrderDeliveredEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been rejected
 */
data class CustomerOrderRejectedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
