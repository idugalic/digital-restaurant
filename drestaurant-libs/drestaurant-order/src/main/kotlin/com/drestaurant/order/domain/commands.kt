package com.drestaurant.order.domain

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'order' bounded context only
 */

internal data class MarkOrderAsDeliveredInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkOrderAsPreparedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkOrderAsReadyForDeliveryInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkOrderAsRejectedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkOrderAsVerifiedByCustomerInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val customerId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal data class MarkOrderAsVerifiedByRestaurantInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val restaurantId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
