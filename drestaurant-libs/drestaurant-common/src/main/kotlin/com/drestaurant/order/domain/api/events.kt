package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.model.OrderDetails

/**
 * An event, noting that order has been initiated (creation has been initiated)
 */
class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been delivered
 */
class OrderDeliveredEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been prepared
 */
class OrderPreparedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order is ready for delivery
 */
class OrderReadyForDeliveryEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been rejected
 */
class OrderRejectedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by customer
 */
class OrderVerifiedByCustomerEvent(override val aggregateIdentifier: String, val customerId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by restaurant
 */
class OrderVerifiedByRestaurantEvent(override val aggregateIdentifier: String, val restaurantId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
