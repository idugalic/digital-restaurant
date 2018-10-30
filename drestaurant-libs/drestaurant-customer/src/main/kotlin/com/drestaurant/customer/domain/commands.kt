package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'customer' bounded context only
 */

internal data class MarkCustomerOrderAsCreatedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkCustomerOrderAsRejectedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class ValidateOrderByCustomerInternalCommand(@TargetAggregateIdentifier val orderId: String, val customerId: String, val orderTotal: Money, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
