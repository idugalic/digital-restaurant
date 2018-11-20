package com.drestaurant.common.domain.api

import com.drestaurant.common.domain.api.model.AuditEntry

/**
 * Abstract command
 *
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class AuditableAbstractCommand(open val auditEntry: AuditEntry)
