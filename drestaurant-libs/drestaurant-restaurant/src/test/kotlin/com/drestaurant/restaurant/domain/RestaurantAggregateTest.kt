package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.model.*
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class RestaurantAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Restaurant>
    private val who = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)
    private var orderId: RestaurantOrderId = RestaurantOrderId("orderId")
    private var restuarantId: RestaurantId = RestaurantId("restuarantId")
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private var orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)


    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Restaurant::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())

        lineItems.add(lineItem)
    }

    @Test
    fun createRestaurantTest() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("id", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")
        val createRestaurantCommand = CreateRestaurantCommand(name, menu, auditEntry)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, createRestaurantCommand.targetAggregateIdentifier, auditEntry)

        fixture
                .given()
                .`when`(createRestaurantCommand)
                .expectEvents(restaurantCreatedEvent)
    }

    @Test(expected = JSR303ViolationException::class)
    fun createRestaurantJSR303ViolationTest() {
        // Setting empty list of items
        val name = "Fancy"
        val menuItems = ArrayList<MenuItem>()

        val menu = RestaurantMenu(menuItems, "v1")
        val createRestaurantCommand = CreateRestaurantCommand(name, menu, auditEntry)

        fixture
                .given()
                .`when`(createRestaurantCommand)
                .expectException(JSR303ViolationException::class.java)
    }


    @Test
    fun createRestaurantOrderTest() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("menuItemId", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restuarantId, auditEntry)

        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(restuarantId, orderDetails, orderId, auditEntry)
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(lineItems, orderId, restuarantId, auditEntry)

        fixture
                .given(restaurantCreatedEvent)
                .`when`(createRestaurantOrderCommand)
                .expectEvents(restaurantOrderCreatedEvent)
    }

    @Test
    fun createRestaurantOrderFailTest() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("WRONG", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restuarantId, auditEntry)

        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(restuarantId, orderDetails, orderId, auditEntry)

        fixture
                .given(restaurantCreatedEvent)
                .`when`(createRestaurantOrderCommand)
                .expectException(UnsupportedOperationException::class.java)
    }

}
