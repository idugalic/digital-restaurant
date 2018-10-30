package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName

/**
 * An event, noting that courier has been created
 */
data class CourierCreatedEvent(val name: PersonName, val maxNumberOfActiveOrders: Int, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been assigned to courier
 */
data class CourierOrderAssignedEvent(override val aggregateIdentifier: String, val courierId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been created
 */
data class CourierOrderCreatedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order has been delivered
 */
data class CourierOrderDeliveredEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that courier order was not assigned to courier
 */
data class CourierOrderNotAssignedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
