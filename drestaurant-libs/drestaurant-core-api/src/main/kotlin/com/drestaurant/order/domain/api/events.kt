package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.order.domain.api.model.OrderDetails
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantId

/**
 * Abstract Order event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class OrderEvent(open val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that order has been initiated (creation has been initiated)
 *
 * @property orderDetails details of a order
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been delivered
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderDeliveredEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been prepared
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderPreparedEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order is ready for delivery
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderReadyForDeliveryEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been rejected
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderRejectedEvent(override val aggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by customer
 *
 * @property aggregateIdentifier aggregate identifier
 * @property customerId identifier of a customer
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderVerifiedByCustomerEvent(override val aggregateIdentifier: OrderId, val customerId: CustomerId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that order has been verified by restaurant
 *
 * @property aggregateIdentifier aggregate identifier
 * @property restaurantId identifier of a restaurant
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
class OrderVerifiedByRestaurantEvent(override val aggregateIdentifier: OrderId, val restaurantId: RestaurantId, override val auditEntry: AuditEntry) : OrderEvent(aggregateIdentifier, auditEntry)
