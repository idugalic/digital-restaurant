package com.drestaurant.query.model

import com.drestaurant.order.domain.model.OrderState
import java.math.BigDecimal

import javax.persistence.*

@Entity
class OrderEntity(
        @Id var id: String,
        var aggregateVersion: Long,
        @ElementCollection var lineItems: List<OrderItemEmbedable>,
        @Enumerated(EnumType.STRING) var state: OrderState
)

@Embeddable
@Access(AccessType.FIELD)
data class OrderItemEmbedable(var menuId: String, var name: String, var price: BigDecimal, var quantity: Int)

