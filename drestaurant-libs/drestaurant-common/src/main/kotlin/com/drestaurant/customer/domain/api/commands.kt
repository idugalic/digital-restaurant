package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * This command is used to construct/register new customer
 */
data class CreateCustomerCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val name: PersonName, val orderLimit: Money, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: PersonName, orderLimit: Money, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), name, orderLimit, auditEntry)
}

/**
 * This command is used to construct new customer order
 */
data class CreateCustomerOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val orderTotal: Money, val customerId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(orderTotal: Money, customerId: String, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), orderTotal, customerId, auditEntry)
}

/**
 * This command is used to mark customer order (targetAggregateIdentifier) as delivered
 */
data class MarkCustomerOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
