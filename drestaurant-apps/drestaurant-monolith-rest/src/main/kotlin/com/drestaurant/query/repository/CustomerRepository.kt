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

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<CustomerEntity>)

    @RestResource(exported = false)
    override fun <S : CustomerEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : CustomerEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()
}
