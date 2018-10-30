package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.RestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'restaurant' bounded context only
 */

internal data class MarkRestaurantOrderAsCreatedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkRestaurantOrderAsRejectedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class ValidateOrderByRestaurantInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, val restaurantId: RestaurantId, val lineItems: List<RestaurantOrderLineItem>, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry)

