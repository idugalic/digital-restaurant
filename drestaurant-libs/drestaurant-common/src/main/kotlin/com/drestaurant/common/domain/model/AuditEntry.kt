package com.drestaurant.common.domain.model

import java.util.*

/**
 * Audit entry holds the information of 'who' and 'when' performed the commands/actions
 */
data class AuditEntry(val who: String, val `when`: Date)
