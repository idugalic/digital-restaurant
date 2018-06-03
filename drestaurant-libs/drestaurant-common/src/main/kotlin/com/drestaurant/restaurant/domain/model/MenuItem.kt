package com.drestaurant.restaurant.domain.model

import com.drestaurant.common.domain.model.Money

class MenuItem {
    var id: String? = null
    var name: String? = null
    var price: Money? = null

    constructor(id: String, name: String, price: Money) {
        this.id = id
        this.name = name
        this.price = price
    }

    constructor() {}

}
