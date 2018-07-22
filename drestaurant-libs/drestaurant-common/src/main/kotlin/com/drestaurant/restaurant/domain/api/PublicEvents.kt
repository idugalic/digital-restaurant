package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem

/**
 * An event, noting that restaurant has been created
 */
class RestaurantCreatedEvent(val name: String, val menu: RestaurantMenu, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been created
 */
class RestaurantOrderCreatedEvent(val lineItems: List<RestaurantOrderLineItem>, val restaurantId: String, orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

/**
 * An event, noting that restaurant order has been prepared
 */
class RestaurantOrderPreparedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

/**
 * An event, noting that restaurant order has been rejected
 */
class RestaurantOrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)