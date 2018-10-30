package com.drestaurant.courier.domain

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.courier.domain.api.CourierOrderCommand
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'courier' bounded context only
 */

internal data class MarkCourierOrderAsAssignedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class MarkCourierOrderAsNotAssignedInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)

internal data class ValidateOrderByCourierInternalCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)
