package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId

/**
 * Abstract Courier event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class CustomerEvent(open val aggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * Abstract CourierOrder event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class CustomerOrderEvent(open val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that 'customer' has been created
 *
 * @property name name of the customer
 * @property orderLimit order limit of a customer
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CustomerCreatedEvent(val name: PersonName, val orderLimit: Money, override val aggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been created
 * @property orderTotal total amount of an order
 * @property aggregateIdentifier aggregate identifier
 * @property customerOrderId customer order identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CustomerOrderCreatedEvent(val orderTotal: Money, override val aggregateIdentifier: CustomerId,  val customerOrderId: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been delivered
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CustomerOrderDeliveredEvent(override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that 'customer order' has been rejected
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CustomerOrderRejectedEvent(override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)
