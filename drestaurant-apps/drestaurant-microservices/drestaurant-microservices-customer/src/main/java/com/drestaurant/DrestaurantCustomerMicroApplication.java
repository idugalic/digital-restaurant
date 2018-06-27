package com.drestaurant;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.customer.domain.api.CreateCustomerCommand;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Calendar;

@SpringBootApplication public class DrestaurantCustomerMicroApplication implements CommandLineRunner {

	@Autowired private CommandGateway commandGateway;

	private static final String WHO = "johndoe";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DrestaurantCustomerMicroApplication.class);
		app.run(args);
	}

	@Override public void run(String... args) throws Exception {
		AuditEntry auditEntry = new AuditEntry(WHO, Calendar.getInstance().getTime());

		//1. ######## Register/Create a Customer ########
		Money orderLimit = new Money(BigDecimal.valueOf(1000000));
		PersonName personName = new PersonName("Ivan", "Dugalic");

		CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(personName, orderLimit, auditEntry);
		commandGateway.send(createCustomerCommand, LoggingCallback.INSTANCE);

	}
}
