package com.drestaurant.query.handler;

import com.drestaurant.order.domain.api.*;
import com.drestaurant.order.domain.model.OrderLineItem;
import com.drestaurant.order.domain.model.OrderState;
import com.drestaurant.query.model.CustomerEntity;
import com.drestaurant.query.model.OrderEntity;
import com.drestaurant.query.model.OrderItemEmbedable;
import com.drestaurant.query.model.RestaurantEntity;
import com.drestaurant.query.repository.CourierRepository;
import com.drestaurant.query.repository.CustomerRepository;
import com.drestaurant.query.repository.OrderRepository;
import com.drestaurant.query.repository.RestaurantRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.SequenceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ProcessingGroup("default") @Component class OrderEventHandler {

	private OrderRepository orderRepository;
	private CustomerRepository customerRepository;
	private RestaurantRepository restaurantRepository;
	private CourierRepository courierRepository;

	@Autowired public OrderEventHandler(OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository,
			CourierRepository courierRepository) {
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
		this.restaurantRepository = restaurantRepository;
		this.courierRepository = courierRepository;
	}

	@EventHandler public void handle(OrderCreationInitiatedEvent event, @SequenceNumber Long aggregateVersion) {
		List<OrderItemEmbedable> orderItems = new ArrayList<>();
		for (OrderLineItem item : event.getOrderDetails().getLineItems()) {
			OrderItemEmbedable orderItem = new OrderItemEmbedable(item.getMenuItemId(), item.getName(), item.getPrice().getAmount(), item.getQuantity());
			orderItems.add(orderItem);
		}
		orderRepository.save(new OrderEntity(event.getAggregateIdentifier(), aggregateVersion, orderItems));
	}

	@EventHandler public void handle(OrderVerifiedByCustomerEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		CustomerEntity customerEntity = customerRepository.findById(event.getCustomerId()).get();
		orderEntity.setCustomer(customerEntity);
		orderEntity.setState(OrderState.VERIFIED_BY_CUSTOMER);
		orderEntity.setAggregateVersion(aggregateVersion);
		orderRepository.save(orderEntity);
	}

	@EventHandler public void handle(OrderVerifiedByRestaurantEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		RestaurantEntity restaurantEntity = restaurantRepository.findById(event.getRestaurantId()).get();
		orderEntity.setAggregateVersion(aggregateVersion);
		orderEntity.setRestaurant(restaurantEntity);
		orderEntity.setState(OrderState.VERIFIED_BY_RESTAURANT);
		orderRepository.save(orderEntity);
	}

	@EventHandler public void handle(OrderPreparedEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		orderEntity.setAggregateVersion(aggregateVersion);
		orderEntity.setState(OrderState.PREPARED);
		orderRepository.save(orderEntity);
	}

	@EventHandler public void handle(OrderReadyForDeliveryEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		orderEntity.setAggregateVersion(aggregateVersion);
		orderEntity.setState(OrderState.READY_FOR_DELIVERY);
		orderRepository.save(orderEntity);
	}

	@EventHandler public void handle(OrderDeliveredEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		orderEntity.setAggregateVersion(aggregateVersion);
		orderEntity.setState(OrderState.DELIVERED);
		orderRepository.save(orderEntity);
	}

	@EventHandler public void handle(OrderRejectedEvent event, @SequenceNumber Long aggregateVersion) {
		OrderEntity orderEntity = orderRepository.findById(event.getAggregateIdentifier()).get();
		orderEntity.setAggregateVersion(aggregateVersion);
		orderEntity.setState(OrderState.REJECTED);
		orderRepository.save(orderEntity);
	}

}
