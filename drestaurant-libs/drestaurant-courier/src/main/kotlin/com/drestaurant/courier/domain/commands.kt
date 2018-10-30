package com.drestaurant.courier.domain

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'courier' bounded context only
 */

internal data class MarkCourierOrderAsAssignedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkCourierOrderAsNotAssignedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class ValidateOrderByCourierInternalCommand(@TargetAggregateIdentifier val orderId: String, val courierId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
