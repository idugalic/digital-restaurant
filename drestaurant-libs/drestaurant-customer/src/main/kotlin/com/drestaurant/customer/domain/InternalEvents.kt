package com.drestaurant.customer.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money

/**
 * Internal events, scoped to 'customer' bounded context only
 */

internal class CustomerNotFoundForOrderInternalEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)

internal class CustomerOrderCreationInitiatedInternalEvent(val orderTotal: Money, val customerId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

internal class OrderValidatedWithErrorByCustomerInternalEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)

internal class OrderValidatedWithSuccessByCustomerInternalEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)
