package com.drestaurant.order.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.order.domain.api.OrderCommand
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'order' bounded context only
 */

internal data class MarkOrderAsDeliveredInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkOrderAsPreparedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkOrderAsReadyForDeliveryInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkOrderAsRejectedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkOrderAsVerifiedByCustomerInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, val customerId: CustomerId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkOrderAsVerifiedByRestaurantInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, val restaurantId: RestaurantId, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry)
