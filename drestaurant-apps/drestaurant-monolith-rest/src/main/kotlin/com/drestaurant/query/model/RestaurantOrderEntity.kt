package com.drestaurant.query.model

import com.drestaurant.restaurant.domain.api.model.RestaurantOrderState
import javax.persistence.*

@Entity
class RestaurantOrderEntity(
        @Id var id: String,
        var aggregateVersion: Long,
        @ElementCollection var lineItems: List<RestaurantOrderItemEmbedable>,
        @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") var restaurant: RestaurantEntity?,
        @Enumerated(EnumType.STRING) var state: RestaurantOrderState
)

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantOrderItemEmbedable(var menuId: String, var name: String, var quantity: Int)

