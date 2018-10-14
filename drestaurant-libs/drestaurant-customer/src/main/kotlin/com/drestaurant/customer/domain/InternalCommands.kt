package com.drestaurant.customer.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import org.axonframework.commandhandling.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'customer' bounded context only
 */

internal class MarkCustomerOrderAsCreatedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkCustomerOrderAsRejectedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class ValidateOrderByCustomerInternalCommand(@TargetAggregateIdentifier val orderId: String, val customerId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
