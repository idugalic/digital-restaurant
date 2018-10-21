package com.drestaurant.query.handler

import com.drestaurant.order.domain.api.*
import com.drestaurant.order.domain.model.OrderState
import com.drestaurant.query.model.OrderEntity
import com.drestaurant.query.model.OrderItemEmbedable
import com.drestaurant.query.repository.CourierRepository
import com.drestaurant.query.repository.CustomerRepository
import com.drestaurant.query.repository.OrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import java.util.*

@Component
@ProcessingGroup("order")
internal class OrderHandler(private val orderRepository: OrderRepository, private val customerRepository: CustomerRepository, private val restaurantRepository: RestaurantRepository, private val courierRepository: CourierRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderCreationInitiatedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderItems = ArrayList<OrderItemEmbedable>()
        for (item in event.orderDetails.lineItems) {
            val orderItem = OrderItemEmbedable(item.menuItemId, item.name, item.price.amount, item.quantity)
            orderItems.add(orderItem)
        }
        orderRepository.save(OrderEntity(event.aggregateIdentifier, aggregateVersion, orderItems, null, null, null, OrderState.CREATE_PENDING))
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderVerifiedByCustomerEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        val customerEntity = customerRepository.findById(event.customerId).get()
        orderEntity.customer = customerEntity
        orderEntity.state = OrderState.VERIFIED_BY_CUSTOMER
        orderEntity.aggregateVersion = aggregateVersion
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderVerifiedByRestaurantEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        val restaurantEntity = restaurantRepository.findById(event.restaurantId).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.restaurant = restaurantEntity
        orderEntity.state = OrderState.VERIFIED_BY_RESTAURANT
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.PREPARED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderReadyForDeliveryEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.READY_FOR_DELIVERY
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderDeliveredEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.DELIVERED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderRejectedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.REJECTED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @ResetHandler // Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table
    fun onReset() = orderRepository.deleteAll()

    private fun broadcastUpdates() = messagingTemplate.convertAndSend("/topic/orders.updates", orderRepository.findAll())
}
