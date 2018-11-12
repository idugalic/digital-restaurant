package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantMenu
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

/**
 * Abstract Restaurant command
 */
abstract class RestaurantCommand(open val targetAggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract RestaurantOrder command
 */
abstract class RestaurantOrderCommand(open val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * This command is used to construct new restaurant
 */
data class CreateRestaurantCommand(val name: String, @field:Valid val menu: RestaurantMenu, @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : RestaurantCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: String, menu: RestaurantMenu, auditEntry: AuditEntry) : this(name, menu, RestaurantId(), auditEntry)
}

/**
 * This command is used to construct new order in restaurant
 */
data class CreateRestaurantOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, @field:Valid val orderDetails: RestaurantOrderDetails, val restaurantId: RestaurantId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(orderDetails: RestaurantOrderDetails, restaurantId: RestaurantId, auditEntry: AuditEntry) : this(RestaurantOrderId(), orderDetails, restaurantId, auditEntry)
}

/**
 * This command is used to mark restaurant order (targetAggregateIdentifier) as prepared
 */
data class MarkRestaurantOrderAsPreparedCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry)
