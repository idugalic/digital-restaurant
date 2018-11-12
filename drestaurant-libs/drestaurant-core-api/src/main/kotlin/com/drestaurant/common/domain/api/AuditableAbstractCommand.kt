package com.drestaurant.common.domain.api

import com.drestaurant.common.domain.api.model.AuditEntry

abstract class AuditableAbstractCommand(open val auditEntry: AuditEntry)
