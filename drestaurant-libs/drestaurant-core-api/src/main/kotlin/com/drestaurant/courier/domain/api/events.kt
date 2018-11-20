package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId

/**
 * Abstract Courier event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class CourierEvent(open val aggregateIdentifier: CourierId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * Abstract CourierOrder event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class CourierOrderEvent(open val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that courier has been created
 *
 * @property name full name of the courier
 * @property maxNumberOfActiveOrders maximal number of active courier orders that courier can deliver
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CourierCreatedEvent(val name: PersonName, val maxNumberOfActiveOrders: Int, override val aggregateIdentifier: CourierId, override val auditEntry: AuditEntry) : CourierEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been assigned to courier
 *
 * @property aggregateIdentifier aggregate identifier
 * @property courierId identifier of the courier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CourierOrderAssignedEvent(override val aggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been created
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CourierOrderCreatedEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been delivered
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CourierOrderDeliveredEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order was not assigned to courier
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class CourierOrderNotAssignedEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)
