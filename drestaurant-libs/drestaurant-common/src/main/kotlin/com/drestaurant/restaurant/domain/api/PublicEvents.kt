package com.drestaurant.restaurant.domain.api

import com.drestaurant.common.domain.event.AuditableAbstractEvent
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantMenu

/**
 * @author: idugalic
 */

class RestaurantCreatedEvent(val name: String, val menu: RestaurantMenu, aggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractEvent(aggregateIdentifier, auditEntry)

class RestaurantOrderCreatedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class RestaurantOrderPreparedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)

class RestaurantOrderRejectedEvent(orderId: String, auditEntry: AuditEntry) : AuditableAbstractEvent(orderId, auditEntry)