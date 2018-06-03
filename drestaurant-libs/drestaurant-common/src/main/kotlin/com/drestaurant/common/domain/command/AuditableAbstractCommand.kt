package com.drestaurant.common.domain.command

import com.drestaurant.common.domain.model.AuditEntry

/**
 *
 * @author: idugalic
 */
open class AuditableAbstractCommand(val auditEntry: AuditEntry)
