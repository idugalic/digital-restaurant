package com.drestaurant.courier.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

/**
 * @author: idugalic
 */

internal class MarkCourierOrderAsAssignedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkCourierOrderAsNotAssignedCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class ValidateOrderByCourierCommand(val orderId: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
