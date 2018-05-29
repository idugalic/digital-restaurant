package com.drestaurant.restaurant.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand;
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent;
import com.drestaurant.restaurant.domain.model.MenuItem;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;

public class RestaurantAggregateTest {

	private FixtureConfiguration<Restaurant> fixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(Restaurant.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
	}

	@Test
	public void createRestaurantTest() throws Exception {
		String name = String.valueOf("Fancy");
		List<MenuItem> menuItems = new ArrayList<>();
		MenuItem item = new MenuItem("id", "name", new Money(BigDecimal.valueOf(100)));
		menuItems.add(item);
		RestaurantMenu menu = new RestaurantMenu(menuItems, "v1");
		CreateRestaurantCommand createRestaurantCommand = new CreateRestaurantCommand(name, menu, auditEntry);
		RestaurantCreatedEvent restaurantCreatedEvent = new RestaurantCreatedEvent(name, menu, createRestaurantCommand.getTargetAggregateIdentifier(), auditEntry);

		fixture
		.given()
		.when(createRestaurantCommand)
		.expectEvents(restaurantCreatedEvent);
	}
	
	@Test(expected = JSR303ViolationException.class)
	public void createRestaurantJSR303ViolationTest() throws Exception {
		// Setting empty list of items
		String name = String.valueOf("Fancy");
		List<MenuItem> menuItems = new ArrayList<>();
		
		RestaurantMenu menu = new RestaurantMenu(menuItems, "v1");
		CreateRestaurantCommand createRestaurantCommand = new CreateRestaurantCommand(name, menu, auditEntry);

		fixture
		.given()
		.when(createRestaurantCommand)
		.expectException(JSR303ViolationException.class);
	}

}
