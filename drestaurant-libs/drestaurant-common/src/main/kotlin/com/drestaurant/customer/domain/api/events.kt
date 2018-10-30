package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId

/**
 * Abstract Courier event
 */
abstract class CustomerEvent(open val aggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * Abstract CourierOrder event
 */
abstract class CustomerOrderEvent(open val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that 'customer' has been created
 */
data class CustomerCreatedEvent(val name: PersonName, val orderLimit: Money, override val aggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been created
 */
data class CustomerOrderCreatedEvent(override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been delivered
 */
data class CustomerOrderDeliveredEvent(override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been rejected
 */
data class CustomerOrderRejectedEvent(override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)
