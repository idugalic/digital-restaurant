package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.model.OrderDetails

/**
 * An event, noting that order has been initiated (creation has been initiated)
 */
class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
/**
 * An event, noting that order has been delivered
 */
class OrderDeliveredEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that order has been prepared
 */
class OrderPreparedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that order is ready for delivery
 */
class OrderReadyForDeliveryEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that order has been rejected
 */
class OrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that order has been verified by customer
 */
class OrderVerifiedByCustomerEvent(orderId: String, val customerId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
/**
 * An event, noting that order has been verified by restaurant
 */
class OrderVerifiedByRestaurantEvent(orderId: String, val restaurantId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)
