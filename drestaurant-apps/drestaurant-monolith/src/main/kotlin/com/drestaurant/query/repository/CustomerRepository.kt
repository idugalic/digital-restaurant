package com.drestaurant.query.repository

import com.drestaurant.query.model.CustomerEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "customers", path = "customers")
interface CustomerRepository : PagingAndSortingRepository<CustomerEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: CustomerEntity)

}
