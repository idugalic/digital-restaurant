package com.drestaurant.query.model

import com.drestaurant.order.domain.api.model.OrderState
import java.math.BigDecimal

import javax.persistence.*

@Entity
class OrderEntity(
        @Id var id: String,
        var aggregateVersion: Long,
        @ElementCollection var lineItems: List<OrderItemEmbedable>,
        @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") var restaurant: RestaurantEntity?,
        @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "CUSTOMER_ID") var customer: CustomerEntity?,
        @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") var courier: CourierEntity?,
        @Enumerated(EnumType.STRING) var state: OrderState
)

@Embeddable
@Access(AccessType.FIELD)
data class OrderItemEmbedable(var menuId: String, var name: String, var price: BigDecimal, var quantity: Int)

