package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.order.domain.api.model.OrderInfo
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

/**
 * Abstract Order command
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class OrderCommand(open val targetAggregateIdentifier: OrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * A command to create/place new order
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property orderInfo details of the order
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class CreateOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: OrderId, @field:Valid val orderInfo: OrderInfo, override val auditEntry: AuditEntry) : OrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(orderInfo: OrderInfo, auditEntry: AuditEntry) : this(OrderId(), orderInfo, auditEntry)
}
