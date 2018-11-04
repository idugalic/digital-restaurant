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

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<CourierEntity>)

    @RestResource(exported = false)
    override fun <S : CourierEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : CourierEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()
}
