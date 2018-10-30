package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.AuditableAbstractEvent
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails

/**
 * Internal events, scoped to 'restaurant' bounded context only
 */

/**
 * Restaurant aggregate event, noting that `restaurant` validated the `restaurant order` with error
 */
internal data class RestaurantValidatedOrderWithErrorInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Restaurant aggregate event, noting that `restaurant` validated the `restaurant order` with success
 */
internal data class RestaurantValidatedOrderWithSuccessInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * Restaurant aggregate event, noting that `restaurant` was not found
 */
internal data class RestaurantNotFoundForOrderInternalEvent(override val aggregateIdentifier: String, val orderId: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

/**
 * RestaurantOrder aggregate event, noting that `restaurant order` creation has been initiated
 */
internal data class RestaurantOrderCreationInitiatedInternalEvent(val orderDetails: RestaurantOrderDetails, val restaurantId: String, override val aggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
