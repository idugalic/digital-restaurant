package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName

/**
 * @author: idugalic
 */

class CustomerCreatedEvent(val name: PersonName, val orderLimit: Money, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

class CustomerOrderCreatedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class CustomerOrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class CustomerOrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
