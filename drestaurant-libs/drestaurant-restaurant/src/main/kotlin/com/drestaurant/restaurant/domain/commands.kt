package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'restaurant' bounded context only
 */

internal data class MarkRestaurantOrderAsCreatedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkRestaurantOrderAsRejectedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class ValidateOrderByRestaurantInternalCommand(@TargetAggregateIdentifier val orderId: String, val restaurantId: String, val lineItems: List<RestaurantOrderLineItem>, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

