package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

/**
 * Abstract Customer command
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class CustomerCommand(open val targetAggregateIdentifier: CustomerId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract CustomerOrder command
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class CustomerOrderCommand(open val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * A command to create/register new 'customer'
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property name full name of the person/customer
 * @property orderLimit order limit of the customer
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class CreateCustomerCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerId, @field:Valid val name: PersonName, val orderLimit: Money, override val auditEntry: AuditEntry) : CustomerCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: PersonName, orderLimit: Money, auditEntry: AuditEntry) : this(CustomerId(), name, orderLimit, auditEntry)
}

/**
 * A command to create new 'customer order' for the customer ([customerId])
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property orderTotal order total price amount
 * @property customerId identifier of the customer for whom we create the order
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class CreateCustomerOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, @field:Valid val orderTotal: Money, val customerId: CustomerId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(orderTotal: Money, customerId: CustomerId, auditEntry: AuditEntry) : this(CustomerOrderId(), orderTotal, customerId, auditEntry)
}

/**
 * A command to mark 'customer order' ([targetAggregateIdentifier]) as delivered
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class MarkCustomerOrderAsDeliveredCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry)
