package com.drestaurant.order.domain

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Internal commands, scoped to 'order' bounded context only
 */

internal class MarkOrderAsDeliveredInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsPreparedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsReadyForDeliveryInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsRejectedInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsVerifiedByCustomerInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val customerId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

internal class MarkOrderAsVerifiedByRestaurantInternalCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val restaurantId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
