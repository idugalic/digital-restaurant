package com.drestaurant.query.model

import com.drestaurant.courier.domain.model.CourierOrderState
import javax.persistence.*

@Entity
class CourierOrderEntity(
        @Id var id: String,
        var aggregateVersion: Long,
        @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") var courier: CourierEntity?,
        @Enumerated(EnumType.STRING) var state: CourierOrderState
)

