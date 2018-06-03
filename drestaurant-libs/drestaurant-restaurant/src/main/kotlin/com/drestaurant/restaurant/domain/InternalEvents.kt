package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails

/**
 * @author: idugalic
 */

internal class OrderValidatedWithErrorByRestaurantEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class OrderValidatedWithSuccessByRestaurantEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class RestaurantNotFoundForOrderEvent(val restaurantId: String, val orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(restaurantId, auditEntry)

internal class RestaurantOrderCreationInitiatedEvent(val orderDetails: RestaurantOrderDetails, val restaurantId: String, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
