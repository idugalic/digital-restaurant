package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId

/**
 * Abstract Courier event
 */
abstract class CourierEvent(open val aggregateIdentifier: CourierId, override val auditEntry: AuditEntry): AuditableAbstractEvent(auditEntry)

/**
 * Abstract CourierOrder event
 */
abstract class CourierOrderEvent(open val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry): AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that courier has been created
 */
data class CourierCreatedEvent(val name: PersonName, val maxNumberOfActiveOrders: Int, override val aggregateIdentifier: CourierId, override val auditEntry: AuditEntry) : CourierEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been assigned to courier
 */
data class CourierOrderAssignedEvent(override val aggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been created
 */
data class CourierOrderCreatedEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been delivered
 */
data class CourierOrderDeliveredEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order was not assigned to courier
 */
data class CourierOrderNotAssignedEvent(override val aggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderEvent(aggregateIdentifier, auditEntry)
