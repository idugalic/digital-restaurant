package com.drestaurant.query.model

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class RestaurantEntity(@Id var id: String, var aggregateVersion: Long, var name: String, @Embedded var menu: RestaurantMenuEmbedable)

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantMenuEmbedable(@ElementCollection var menuItems: List<MenuItemEmbedable>, var menuVersion: String)

@Embeddable
@Access(AccessType.FIELD)
data class MenuItemEmbedable(var menuId: String, var name: String, var price: BigDecimal)

