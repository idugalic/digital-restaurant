package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.model.RestaurantMenu
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem

/**
 * An event, noting that restaurant has been created
 */
data class RestaurantCreatedEvent(val name: String, val menu: RestaurantMenu, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been created
 */
data class RestaurantOrderCreatedEvent(val lineItems: List<RestaurantOrderLineItem>, val restaurantId: String, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been prepared
 */
data class RestaurantOrderPreparedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * An event, noting that restaurant order has been rejected
 */
data class RestaurantOrderRejectedEvent(override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)