package com.drestaurant.query.handler;

import com.drestaurant.query.model.MenuItemEmbedable;
import com.drestaurant.query.model.RestaurantEntity;
import com.drestaurant.query.model.RestaurantMenuEmbedable;
import com.drestaurant.query.repository.RestaurantRepository;
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent;
import com.drestaurant.restaurant.domain.model.MenuItem;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.SequenceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ProcessingGroup("default") @Component class RestaurantEventHandler {

	private RestaurantRepository repository;

	@Autowired public RestaurantEventHandler(RestaurantRepository repository) {
		this.repository = repository;
	}

	@EventHandler public void handle(RestaurantCreatedEvent event, @SequenceNumber Long aggregateVersion) {
		RestaurantMenuEmbedable menu = new RestaurantMenuEmbedable();
		List<MenuItemEmbedable> menuItems = new ArrayList<>();
		for (MenuItem item : event.getMenu().getMenuItems()) {
			MenuItemEmbedable menuItem = new MenuItemEmbedable(item.getId(), item.getName(), item.getPrice().getAmount());
			menuItems.add(menuItem);
		}
		menu.setMenuItems(menuItems);
		menu.setMenuVersion(event.getMenu().getMenuVersion());
		repository.save(new RestaurantEntity(event.getAggregateIdentifier(), aggregateVersion, event.getName(), menu));
	}

}
