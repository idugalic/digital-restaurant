package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.model.OrderInfo
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * This command is used to construct/place new order
 */
class CreateOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val orderInfo: OrderInfo, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(orderInfo: OrderInfo, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), orderInfo, auditEntry)
}
