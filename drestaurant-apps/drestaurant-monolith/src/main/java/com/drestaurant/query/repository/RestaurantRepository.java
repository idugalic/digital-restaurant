package com.drestaurant.query.repository;

import com.drestaurant.query.model.RestaurantEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "restaurants", path = "restaurants") public interface RestaurantRepository
		extends PagingAndSortingRepository<RestaurantEntity, String> {

	@Override @RestResource(exported = false) RestaurantEntity save(RestaurantEntity entity);

	@Override @RestResource(exported = false) void deleteById(String aLong);

	@Override @RestResource(exported = false) void delete(RestaurantEntity entity);

}
