package com.drestaurant.query.repository

import com.drestaurant.query.model.CourierEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "couriers", path = "couriers")
interface CourierRepository : PagingAndSortingRepository<CourierEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: CourierEntity)

}
