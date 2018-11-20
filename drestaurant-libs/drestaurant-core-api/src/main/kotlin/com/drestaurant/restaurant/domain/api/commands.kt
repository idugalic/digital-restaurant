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
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class RestaurantCommand(open val targetAggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract RestaurantOrder command
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class RestaurantOrderCommand(open val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * A command to create new 'restaurant'
 *
 * @property name name of the restaurant
 * @property menu menu of the restaurant
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class CreateRestaurantCommand(val name: String, @field:Valid val menu: RestaurantMenu, @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) : RestaurantCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: String, menu: RestaurantMenu, auditEntry: AuditEntry) : this(name, menu, RestaurantId(), auditEntry)
}

/**
 * A command to crete new 'restaurant order'
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property orderDetails order details holds the order line items
 * @property restaurantId identifier of the restaurant to which the order is created
 */
data class CreateRestaurantOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, @field:Valid val orderDetails: RestaurantOrderDetails, val restaurantId: RestaurantId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(orderDetails: RestaurantOrderDetails, restaurantId: RestaurantId, auditEntry: AuditEntry) : this(RestaurantOrderId(), orderDetails, restaurantId, auditEntry)
}

/**
 * A command to mark 'restaurant order' ([targetAggregateIdentifier]) as prepared
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class MarkRestaurantOrderAsPreparedCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId, override val auditEntry: AuditEntry) : RestaurantOrderCommand(targetAggregateIdentifier, auditEntry)
