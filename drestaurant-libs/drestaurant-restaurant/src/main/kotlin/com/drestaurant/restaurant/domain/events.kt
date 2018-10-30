package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.RestaurantEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId

/**
 * Internal events, scoped to 'restaurant' bounded context only
 */

/**
 * Restaurant aggregate event, noting that `restaurant` validated the `restaurant order` with error
 */
internal data class RestaurantValidatedOrderWithErrorInternalEvent(override val aggregateIdentifier: RestaurantId, val orderId: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantEvent(aggregateIdentifier, auditEntry)

/**
 * Restaurant aggregate event, noting that `restaurant` validated the `restaurant order` with success
 */
internal data class RestaurantValidatedOrderWithSuccessInternalEvent(override val aggregateIdentifier: RestaurantId, val orderId: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantEvent(aggregateIdentifier, auditEntry)

/**
 * Restaurant aggregate event, noting that `restaurant` was not found
 */
internal data class RestaurantNotFoundForOrderInternalEvent(override val aggregateIdentifier: RestaurantId, val orderId: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantEvent(aggregateIdentifier, auditEntry)

/**
 * RestaurantOrder aggregate event, noting that `restaurant order` creation has been initiated
 */
internal data class RestaurantOrderCreationInitiatedInternalEvent(val orderDetails: RestaurantOrderDetails, val restaurantId: RestaurantId, override val aggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderEvent(aggregateIdentifier, auditEntry)
