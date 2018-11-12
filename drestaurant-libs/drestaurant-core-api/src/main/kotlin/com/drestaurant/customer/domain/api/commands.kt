package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * Abstract Customer command
 */
abstract class CustomerCommand(open val targetAggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract CustomerOrder command
 */
abstract class CustomerOrderCommand(open val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * This command is used to construct/register new customer
 */
data class CreateCustomerCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerId, @field:Valid val name: PersonName, val orderLimit: Money, override val auditEntry: AuditEntry) : CustomerCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: PersonName, orderLimit: Money, auditEntry: AuditEntry) : this(CustomerId(), name, orderLimit, auditEntry)
}

/**
 * This command is used to construct new customer order
 */
data class CreateCustomerOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, @field:Valid val orderTotal: Money, val customerId: CustomerId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(orderTotal: Money, customerId: CustomerId, auditEntry: AuditEntry) : this(CustomerOrderId(), orderTotal, customerId, auditEntry)
}

/**
 * This command is used to mark customer order (targetAggregateIdentifier) as delivered
 */
data class MarkCustomerOrderAsDeliveredCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry)
