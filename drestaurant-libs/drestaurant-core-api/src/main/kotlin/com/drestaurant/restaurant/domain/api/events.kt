package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantMenu
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem

/**
 * Abstract Restaurant event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class RestaurantEvent(open val aggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * Abstract RestaurantOrder event
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
abstract class RestaurantOrderEvent(open val aggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : AuditableAbstractEvent(auditEntry)

/**
 * An event, noting that restaurant has been created
 *
 * @property name name of the restaurant
 * @property menu restaurant menu
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class RestaurantCreatedEvent(val name: String, val menu: RestaurantMenu, override val aggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : RestaurantEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been created
 *
 * @property lineItems restaurant order line items
 * @property restaurantOrderId identifier of the restaurant order
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class RestaurantOrderCreatedEvent(val lineItems: List<RestaurantOrderLineItem>, val restaurantOrderId: RestaurantOrderId, override val aggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : RestaurantEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been prepared
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class RestaurantOrderPreparedEvent(override val aggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been rejected
 *
 * @property aggregateIdentifier aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' triggered the event
 */
data class RestaurantOrderRejectedEvent(override val aggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderEvent(aggregateIdentifier, auditEntry)
