package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.model.OrderDetails

/**
 * @author: idugalic
 */
class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

class OrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class OrderPreparedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class OrderReadyForDeliveryEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class OrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class OrderVerifiedByCustomerEvent(orderId: String, val customerId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class OrderVerifiedByRestaurantEvent(orderId: String, val restaurantId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
