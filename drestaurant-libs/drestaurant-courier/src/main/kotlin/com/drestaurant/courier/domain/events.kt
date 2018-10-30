package com.drestaurant.courier.domain

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry

/**
 * Internal events, scoped to 'courier' bounded context only
 */


/**
 * Courier aggregate event, noting that `courier` was not found
 */
internal data class CourierNotFoundForOrderInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * CourierOrder aggregate event, noting that 'courier order' assigning to `courier' has been initiated
 */
internal data class CourierOrderAssigningInitiatedInternalEvent(val courierId: String, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Courier aggregate event, noting that `courier` validated the `courier order` with error
 */
internal data class CourierValidatedOrderWithErrorInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Courier aggregate event, noting that `courier` validated the `courier order` with error
 */
internal data class CourierValidatedOrderWithSuccessInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
