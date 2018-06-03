package com.drestaurant;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) public class DrestaurantMonolithApplication implements CommandLineRunner {

	@Autowired private CommandGateway commandGateway;

	private static final String WHO = "johndoe";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DrestaurantMonolithApplication.class);
		app.run(args);
	}

	@Override public void run(String... args) throws Exception {
		//		AuditEntry auditEntry = new AuditEntry(WHO);
		//
		//		//1. ######## Register/Create a Customer ########
		//	    Money orderLimit = new Money(BigDecimal.valueOf(1000000));
		//		PersonName personName = new PersonName("Ivan", "Dugalic");
		//
		//		CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(personName ,orderLimit, auditEntry);
		//		String consumerId = commandGateway.sendAndWait(createCustomerCommand);
		//
		//		//2. ######### Hire/Create a Courier
		//		Integer maxNumberOfActiveOrders = 5;
		//		PersonName courierName = new PersonName("Ivan", "Dugalic");
		//		CreateCourierCommand createCourierCommand = new CreateCourierCommand(courierName, maxNumberOfActiveOrders, auditEntry);
		//		String courierId = commandGateway.sendAndWait(createCourierCommand);
		//
		//		//3. ######## Create a Restaurant & Menu ########
		//		String restaurantName = String.valueOf("Fancy");
		//		List<MenuItem> menuItems = new ArrayList<>();
		//		MenuItem item1 = new MenuItem("menuItemId1", "name1", new Money(BigDecimal.valueOf(100)));
		//		MenuItem item2 = new MenuItem("menuItemId2", "name2", new Money(BigDecimal.valueOf(110)));
		//		menuItems.add(item1);
		//		menuItems.add(item2);
		//		RestaurantMenu menu = new RestaurantMenu(menuItems, "version1");
		//
		//		CreateRestaurantCommand createRestaurantCommand = new CreateRestaurantCommand(restaurantName, menu, auditEntry);
		//		String restaurantId = commandGateway.sendAndWait(createRestaurantCommand);
		//
		//		//4. ######## Place/Create an order ########
		//		List<OrderLineItem> lineItems = new ArrayList<OrderLineItem>();
		//		OrderLineItem lineItem1 = new OrderLineItem("menuItemId1","name1", new Money(100), 2);
		//		OrderLineItem lineItem2 = new OrderLineItem("menuItemId2","name2", new Money(110), 3);
		//		lineItems.add(lineItem1);
		//		lineItems.add(lineItem2);
		//		OrderInfo orderInfo = new OrderInfo(consumerId, restaurantId, lineItems);
		//
		//		CreateOrderCommand createOrderCommand = new CreateOrderCommand(orderInfo, auditEntry);
		//		String orderId = commandGateway.sendAndWait(createOrderCommand);
		//
		//		//5. ########## Mark restaurant order as prepared and ready for delivery ##########
		//		MarkRestaurantOrderAsPreparedCommand markRestaurantOrderAsPreparedCommand = new MarkRestaurantOrderAsPreparedCommand("restaurantOrder_" + orderId, auditEntry);
		//		commandGateway.sendAndWait(markRestaurantOrderAsPreparedCommand);
		//
		//		//6. ########## Assign courier order to courier (Courier picks the order that is ready for delivery) ##########
		//		AssignCourierOrderToCourierCommand assignCourierOrderToCourierCommand = new AssignCourierOrderToCourierCommand("courierOrder_" + orderId, courierId, auditEntry);
		//		commandGateway.sendAndWait(assignCourierOrderToCourierCommand);
		//
		//		//7. ##########  Mark courier order as delivered ##########
		//		MarkCourierOrderAsDeliveredCommand markCourierOrderAsDeliveredCommand = new MarkCourierOrderAsDeliveredCommand("courierOrder_" + orderId, auditEntry);
		//		commandGateway.sendAndWait(markCourierOrderAsDeliveredCommand);
		//
	}
}
