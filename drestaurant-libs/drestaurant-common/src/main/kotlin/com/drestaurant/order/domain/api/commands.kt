package com.drestaurant.order.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.order.domain.api.model.OrderInfo
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * This command is used to construct/place new order
 */
data class CreateOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val orderInfo: OrderInfo, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(orderInfo: OrderInfo, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), orderInfo, auditEntry)
}
