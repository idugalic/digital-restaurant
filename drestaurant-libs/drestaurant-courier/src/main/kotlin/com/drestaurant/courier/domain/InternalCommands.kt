package com.drestaurant.courier.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import org.axonframework.commandhandling.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'courier' bounded context only
 */

internal class MarkCourierOrderAsAssignedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkCourierOrderAsNotAssignedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class ValidateOrderByCourierInternalCommand(@TargetAggregateIdentifier val orderId: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
