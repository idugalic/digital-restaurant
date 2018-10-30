package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.customer.domain.api.CustomerEvent
import com.drestaurant.customer.domain.api.CustomerOrderEvent
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId

/**
 * Internal events, scoped to 'customer' bounded context only
 */


/**
 * Customer aggregate event, noting that `customer` was not found
 */
internal data class CustomerNotFoundForOrderInternalEvent(override val aggregateIdentifier: CustomerId, val orderId: CustomerOrderId, val orderTotal: Money, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)

/**
 * CustomerOrder aggregate event, noting that `customer order` creation has been initiated
 */
internal data class CustomerOrderCreationInitiatedInternalEvent(val orderTotal: Money, val customerId: CustomerId, override val aggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderEvent(aggregateIdentifier, auditEntry)

/**
 * Customer aggregate event, noting that `customer` validated the `customer order` with error
 */
internal data class CustomerValidatedOrderWithErrorInternalEvent(override val aggregateIdentifier: CustomerId, val orderId: CustomerOrderId, val orderTotal: Money, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)

/**
 * Customer aggregate event, noting that `customer` validated the `customer order` with success
 */
internal data class CustomerValidatedOrderWithSuccessInternalEvent(override val aggregateIdentifier: CustomerId, val orderId: CustomerOrderId, val orderTotal: Money, override val auditEntry: AuditEntry) : CustomerEvent(aggregateIdentifier, auditEntry)
