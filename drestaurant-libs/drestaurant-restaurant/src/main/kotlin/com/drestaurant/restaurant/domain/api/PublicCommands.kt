package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author: idugalic
 */

class CreateRestaurantCommand(@field:NotNull val name: String, @field:Valid val menu: RestaurantMenu, @TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: String, menu: RestaurantMenu, auditEntry: AuditEntry) : this(name, menu, UUID.randomUUID().toString(), auditEntry)

}

class CreateRestaurantOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:NotNull @field:Valid val orderDetails: RestaurantOrderDetails, val restaurantId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(orderDetails: RestaurantOrderDetails, restaurantId: String, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), orderDetails, restaurantId, auditEntry)

}

class MarkRestaurantOrderAsPreparedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
