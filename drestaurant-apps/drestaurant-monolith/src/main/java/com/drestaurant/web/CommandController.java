package com.drestaurant.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand;
import com.drestaurant.courier.domain.api.CreateCourierCommand;
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand;
import com.drestaurant.customer.domain.api.CreateCustomerCommand;
import com.drestaurant.order.domain.api.CreateOrderCommand;
import com.drestaurant.order.domain.model.OrderInfo;
import com.drestaurant.order.domain.model.OrderLineItem;
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand;
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand;
import com.drestaurant.restaurant.domain.model.MenuItem;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;

@RestController
@RequestMapping(value = "/api/command")
public class CommandController {

	private CommandGateway commandGateway;

	private String getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		}
		return null;
	}

	private AuditEntry getAuditEntry() {
		return new AuditEntry(getCurrentUser());
	}

	@Autowired
	public CommandController(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@RequestMapping(value = "/customer/createcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createCustomer(@RequestBody CreateCustomerRequest request, HttpServletResponse response) {
		PersonName personName = new PersonName(request.getFirstName(), request.getLastName());
		Money orderLimit = new Money(request.getOrderLimit());
		CreateCustomerCommand command = new CreateCustomerCommand(personName, orderLimit, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}

	@RequestMapping(value = "/courier/createcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createCourier(@RequestBody CreateCourierRequest request, HttpServletResponse response) {
		PersonName personName = new PersonName(request.getFirstName(), request.getLastName());
		CreateCourierCommand command = new CreateCourierCommand(personName, request.getMaxNumberOfActiveOrders(), getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}

	@RequestMapping(value = "/restaurant/createcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createRestaurant(@RequestBody CreateRestaurantRequest request, HttpServletResponse response) {
		List<MenuItem> menuItems = new ArrayList<>();
		for (MenuItemRequest itemRequest : request.getMenuItems()) {
			MenuItem item = new MenuItem(itemRequest.getId(), itemRequest.getName(), new Money(itemRequest.getPrice()));
			menuItems.add(item);
		}
		RestaurantMenu menu = new RestaurantMenu(menuItems, "ver.0");
		CreateRestaurantCommand command = new CreateRestaurantCommand(request.getName(), menu, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@RequestMapping(value = "/order/createcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createOrder(@RequestBody CreateOrderRequest request, HttpServletResponse response) {
		List<OrderLineItem> lineItems = new ArrayList<OrderLineItem>();
		for (OrderItemRequest itemRequest : request.getOrderItems()) {
			OrderLineItem item = new OrderLineItem(itemRequest.getId(), itemRequest.getName(), new Money(itemRequest.getPrice()), itemRequest.getQuantity());
			lineItems.add(item);
		}
		OrderInfo orderInfo = new OrderInfo(request.getCustomerId(), request.getRestaurantId(), lineItems);
		CreateOrderCommand command = new CreateOrderCommand(orderInfo, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@RequestMapping(value = "/restaurant/order/{id}/markpreparedcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void markRestaurantOrderAsPrepared(@PathVariable String id, HttpServletResponse response) {
		MarkRestaurantOrderAsPreparedCommand command = new MarkRestaurantOrderAsPreparedCommand(id, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@RequestMapping(value = "/courier/{cid}/order/{oid}/assigncommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void assignOrderToCourier(@PathVariable String cid, @PathVariable String oid, HttpServletResponse response) {
		AssignCourierOrderToCourierCommand command = new AssignCourierOrderToCourierCommand(oid, cid, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@RequestMapping(value = "/courier/order/{id}/markdeliveredcommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void markCourierOrderAsDelivered(@PathVariable String id, HttpServletResponse response) {
		MarkCourierOrderAsDeliveredCommand command = new MarkCourierOrderAsDeliveredCommand(id, getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	
}
