package com.drestaurant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand;
import com.drestaurant.restaurant.domain.model.MenuItem;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;

@SpringBootApplication
public class DrestaurantRestaurantMicroApplication implements CommandLineRunner {

	@Autowired
	private CommandGateway commandGateway;
	
    private static final String WHO = "johndoe";
	
	public static void main(String[] args) {
		SpringApplication  app = new SpringApplication(DrestaurantRestaurantMicroApplication.class);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		AuditEntry auditEntry = new AuditEntry(WHO);		

		
		//1. ######## Create a Restaurant & Menu ########
		String restaurantName = String.valueOf("Fancy");
		List<MenuItem> menuItems = new ArrayList<>();
		MenuItem item1 = new MenuItem("menuItemId1", "name1", new Money(BigDecimal.valueOf(100)));
		MenuItem item2 = new MenuItem("menuItemId2", "name2", new Money(BigDecimal.valueOf(110)));
		menuItems.add(item1);
		menuItems.add(item2);
		RestaurantMenu menu = new RestaurantMenu(menuItems, "version1");
		
		CreateRestaurantCommand createRestaurantCommand = new CreateRestaurantCommand(restaurantName, menu, auditEntry);
		String restaurantId = commandGateway.sendAndWait(createRestaurantCommand);
	
		//2. ########## Mark restaurant order as prepared and ready for delivery ##########
//		MarkRestaurantOrderAsPreparedCommand markRestaurantOrderAsPreparedCommand = new MarkRestaurantOrderAsPreparedCommand("restaurantOrder_" + orderId, auditEntry);
//		commandGateway.sendAndWait(markRestaurantOrderAsPreparedCommand);
		
	
		
	}
}
