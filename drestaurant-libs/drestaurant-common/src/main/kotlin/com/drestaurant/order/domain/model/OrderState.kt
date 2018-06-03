package com.drestaurant.order.domain.model

/**
 * @author: idugalic
 */
enum class OrderState {
    CREATE_PENDING,
    VERIFIED_BY_CUSTOMER,
    VERIFIED_BY_RESTAURANT,
    PREPARED,
    READY_FOR_DELIVERY,
    DELIVERED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED
}