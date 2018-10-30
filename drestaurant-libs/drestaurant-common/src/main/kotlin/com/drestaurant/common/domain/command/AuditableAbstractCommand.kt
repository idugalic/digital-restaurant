package com.drestaurant.common.domain.command

import com.drestaurant.common.domain.model.AuditEntry

abstract class AuditableAbstractCommand(open val auditEntry: AuditEntry)
