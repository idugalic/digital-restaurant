package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.customer.domain.api.CustomerOrderCommand
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'customer' bounded context only
 */

internal data class MarkCustomerOrderAsCreatedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkCustomerOrderAsRejectedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class ValidateOrderByCustomerInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CustomerOrderId, val customerId: CustomerId, val orderTotal: Money, override val auditEntry: AuditEntry) : CustomerOrderCommand(targetAggregateIdentifier, auditEntry)
