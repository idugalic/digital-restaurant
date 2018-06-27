package com.drestaurant.customer.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money

/**
 * @author: idugalic
 */

internal class CustomerNotFoundForOrderEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)

internal class CustomerOrderCreationInitiatedEvent(val orderTotal: Money, val customerId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

internal class OrderValidatedWithErrorByCustomerEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)

internal class OrderValidatedWithSuccessByCustomerEvent(val customerId: String, val orderId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractEvent(customerId, auditEntry)
