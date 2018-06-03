package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

/**
 * @author: idugalic
 */

internal class MarkRestaurantOrderAsCreatedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkRestaurantOrderAsRejectedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class ValidateOrderByRestaurantCommand(val orderId: String, val restaurantId: String, val lineItems: List<RestaurantOrderLineItem>, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

