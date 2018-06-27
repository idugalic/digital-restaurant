package com.drestaurant.customer.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

/**
 * @author: idugalic
 */

internal class MarkCustomerOrderAsCreatedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkCustomerOrderAsRejectedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class ValidateOrderByCustomerCommand(val orderId: String, val customerId: String, val orderTotal: Money, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
