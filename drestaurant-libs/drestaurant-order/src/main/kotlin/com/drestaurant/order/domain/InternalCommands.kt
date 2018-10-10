package com.drestaurant.order.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import org.axonframework.commandhandling.TargetAggregateIdentifier

/**
 *
 * @author: idugalic
 */

internal class MarkOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsPreparedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsReadyForDeliveryCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsRejectedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsVerifiedByCustomerCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val customerId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsVerifiedByRestaurantCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val restaurantId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
