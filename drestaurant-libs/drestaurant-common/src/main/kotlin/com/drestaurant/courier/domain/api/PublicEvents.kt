package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName

/**
 * @author: idugalic
 */

class CourierCreatedEvent(val name: PersonName, val maxNumberOfActiveOrders: Int, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

class CourierOrderAssignedEvent(orderId: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class CourierOrderCreatedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class CourierOrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)