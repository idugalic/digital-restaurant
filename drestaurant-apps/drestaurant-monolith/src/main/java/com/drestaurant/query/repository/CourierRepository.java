package com.drestaurant.query.repository;

import com.drestaurant.query.model.CourierEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "couriers", path = "couriers") public interface CourierRepository
		extends PagingAndSortingRepository<CourierEntity, String> {

	@Override @RestResource(exported = false) CourierEntity save(CourierEntity entity);

	@Override @RestResource(exported = false) void deleteById(String aLong);

	@Override @RestResource(exported = false) void delete(CourierEntity entity);

}
