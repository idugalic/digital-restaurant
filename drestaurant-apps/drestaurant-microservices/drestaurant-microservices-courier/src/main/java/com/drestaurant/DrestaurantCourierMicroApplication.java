package com.drestaurant;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.courier.domain.api.CreateCourierCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;

@SpringBootApplication public class DrestaurantCourierMicroApplication implements CommandLineRunner {

	@Autowired private CommandGateway commandGateway;

	private static final String WHO = "johndoe";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DrestaurantCourierMicroApplication.class);
		app.run(args);
	}

	@Override public void run(String... args) throws Exception {
		AuditEntry auditEntry = new AuditEntry(WHO, Calendar.getInstance().getTime());

		//1. ######### Hire/Create a Courier
		Integer maxNumberOfActiveOrders = 5;
		PersonName courierName = new PersonName("Ivan", "Dugalic");
		CreateCourierCommand createCourierCommand = new CreateCourierCommand(courierName, maxNumberOfActiveOrders, auditEntry);
		String courierId = commandGateway.sendAndWait(createCourierCommand);

		//2. ########## Assign courier order to courier (Courier picks the order that is ready for delivery) ##########
		//		AssignCourierOrderToCourierCommand assignCourierOrderToCourierCommand = new AssignCourierOrderToCourierCommand("courierOrder_" + orderId, courierId, auditEntry);
		//		commandGateway.sendAndWait(assignCourierOrderToCourierCommand);

		//3. ##########  Mark courier order as delivered ##########
		//		MarkCourierOrderAsDeliveredCommand markCourierOrderAsDeliveredCommand = new MarkCourierOrderAsDeliveredCommand("courierOrder_" + orderId, auditEntry);
		//		commandGateway.sendAndWait(markCourierOrderAsDeliveredCommand);

	}
}
