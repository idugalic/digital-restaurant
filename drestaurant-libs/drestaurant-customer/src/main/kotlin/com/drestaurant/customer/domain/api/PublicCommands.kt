package com.drestaurant.customer.domain.api

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * @author: idugalic
 */

class CreateCustomerCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val name: PersonName, val orderLimit: Money, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: PersonName, orderLimit: Money, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), name, orderLimit, auditEntry)

}

class CreateCustomerOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val orderTotal: Money, val customerId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(orderTotal: Money, customerId: String, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), orderTotal, customerId, auditEntry)

}

class MarkCustomerOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
