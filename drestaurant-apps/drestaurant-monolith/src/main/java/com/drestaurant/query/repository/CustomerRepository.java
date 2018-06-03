package com.drestaurant.query.repository;

import com.drestaurant.query.model.CustomerEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "customers", path = "customers") public interface CustomerRepository
		extends PagingAndSortingRepository<CustomerEntity, String> {

	@Override @RestResource(exported = false) CustomerEntity save(CustomerEntity entity);

	@Override @RestResource(exported = false) void deleteById(String aLong);

	@Override @RestResource(exported = false) void delete(CustomerEntity entity);

}
