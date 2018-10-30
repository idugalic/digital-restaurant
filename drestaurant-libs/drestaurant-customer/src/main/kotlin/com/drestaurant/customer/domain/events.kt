package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money

/**
 * Internal events, scoped to 'customer' bounded context only
 */


/**
 * Customer aggregate event, noting that `customer` was not found
 */
internal data class CustomerNotFoundForOrderInternalEvent(override val aggregateIdentifier: String, val orderId: String, val orderTotal: Money, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * CustomerOrder aggregate event, noting that `customer order` creation has been initiated
 */
internal data class CustomerOrderCreationInitiatedInternalEvent(val orderTotal: Money, val customerId: String, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Customer aggregate event, noting that `customer` validated the `customer order` with error
 */
internal data class CustomerValidatedOrderWithErrorInternalEvent(override val aggregateIdentifier: String, val orderId: String, val orderTotal: Money, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Customer aggregate event, noting that `customer` validated the `customer order` with success
 */
internal data class CustomerValidatedOrderWithSuccessInternalEvent(override val aggregateIdentifier: String, val orderId: String, val orderTotal: Money, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
