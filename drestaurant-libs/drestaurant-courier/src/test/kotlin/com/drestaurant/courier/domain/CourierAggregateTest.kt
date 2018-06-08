package com.drestaurant.courier.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.courier.domain.api.CreateCourierCommand
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.AxonAssertionError
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.util.*

class CourierAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Courier>
    private lateinit var auditEntry: AuditEntry
    private lateinit var auditEntry2: AuditEntry
    private var maxNumberOfActiveOrders: Int = 5
    private val WHO = "johndoe"

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Courier::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
        auditEntry = AuditEntry(WHO, Calendar.getInstance().time)
        auditEntry2 = AuditEntry(WHO + "2", Calendar.getInstance().time)
    }

    @Test
    fun createCourierTest() {
        val name = PersonName("Ivan", "Dugalic")
        val createCourierCommand = CreateCourierCommand(name, maxNumberOfActiveOrders, auditEntry)
        val courierCreatedEvent = CourierCreatedEvent(name, maxNumberOfActiveOrders, createCourierCommand.targetAggregateIdentifier, auditEntry)

        fixture
                .given()
                .`when`(createCourierCommand)
                .expectEvents(courierCreatedEvent)
    }

    @Test(expected = AxonAssertionError::class)
    fun createCourierAxonAssertionErrorTest() {
        val name = PersonName("Ivan", "Dugalic")
        val createCourierCommand = CreateCourierCommand(name, maxNumberOfActiveOrders, auditEntry)

        fixture
                .given()
                .`when`(createCourierCommand)
                .expectException(AxonAssertionError::class.java)

    }

}
