package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.order.domain.api.model.OrderDetails
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantId

/**
 * Abstract Order event
 */
abstract class OrderEvent(open val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that order has been initiated (creation has been initiated)
 */
class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been delivered
 */
class OrderDeliveredEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been prepared
 */
class OrderPreparedEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order is ready for delivery
 */
class OrderReadyForDeliveryEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been rejected
 */
class OrderRejectedEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by customer
 */
class OrderVerifiedByCustomerEvent(override val aggregateIdentifier: OrderId, val customerId: CustomerId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by restaurant
 */
class OrderVerifiedByRestaurantEvent(override val aggregateIdentifier: OrderId, val restaurantId: RestaurantId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)
