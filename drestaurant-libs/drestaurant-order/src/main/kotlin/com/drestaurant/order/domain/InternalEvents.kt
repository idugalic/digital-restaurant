package com.drestaurant.order.domain

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.model.OrderDetails

/**
 *
 * @author: idugalic
 */

internal class OrderCreationInitiatedEvent(val orderDetails: OrderDetails, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)
