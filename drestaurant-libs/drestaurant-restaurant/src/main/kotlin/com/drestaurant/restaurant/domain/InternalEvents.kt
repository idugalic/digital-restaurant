package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails

/**
 * Internal events, scoped to 'restaurant' bounded context only
 */

internal class OrderValidatedWithErrorByRestaurantInternalEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class OrderValidatedWithSuccessByRestaurantInternalEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class RestaurantNotFoundForOrderInternalEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class RestaurantOrderCreationInitiatedInternalEvent(val orderDetails: RestaurantOrderDetails, val restaurantId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
