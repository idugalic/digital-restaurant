package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.util.*

class RestaurantOrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<RestaurantOrder>
    private var orderId: RestaurantOrderId = RestaurantOrderId("orderId")
    private var restuarantId: RestaurantId = RestaurantId("restuarantId")
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val who = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(RestaurantOrder::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())

        lineItems.add(lineItem)
    }

    @Test
    fun markOrderAsPreparedTest() {
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(lineItems, orderId, restuarantId, auditEntry)

        val markRestaurantOrderAsPreparedCommand = MarkRestaurantOrderAsPreparedCommand(orderId, auditEntry)
        val restaurantOrderPreparedEvent = RestaurantOrderPreparedEvent(orderId, auditEntry)

        fixture.given(restaurantOrderCreatedEvent).`when`(markRestaurantOrderAsPreparedCommand)
                .expectEvents(restaurantOrderPreparedEvent)
    }
}
