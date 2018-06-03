package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author: idugalic
 */

class CreateCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:NotNull @field:Valid val name: PersonName, val maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: PersonName, maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), name, maxNumberOfActiveOrders, auditEntry)

}

class CreateCourierOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), auditEntry) {}

}

class AssignCourierOrderToCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

class MarkCourierOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

