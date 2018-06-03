package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.domain.model.MenuItem
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.messaging.interceptors.JSR303ViolationException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class RestaurantAggregateTest {

    private var fixture: FixtureConfiguration<Restaurant>? = null
    private val WHO = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(WHO);


    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Restaurant::class.java)
        fixture!!.registerCommandDispatchInterceptor(BeanValidationInterceptor())
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

        fixture!!.given().`when`(createRestaurantCommand).expectEvents(restaurantCreatedEvent)
    }

    @Test(expected = JSR303ViolationException::class)
    fun createRestaurantJSR303ViolationTest() {
        // Setting empty list of items
        val name = "Fancy"
        val menuItems = ArrayList<MenuItem>()

        val menu = RestaurantMenu(menuItems, "v1")
        val createRestaurantCommand = CreateRestaurantCommand(name, menu, auditEntry)

        fixture!!.given().`when`(createRestaurantCommand).expectException(JSR303ViolationException::class.java)
    }

}
