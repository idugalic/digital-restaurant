package com.drestaurant.query.repository

import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.model.RestaurantEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "restaurants", path = "restaurants")
interface RestaurantRepository : PagingAndSortingRepository<RestaurantEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: RestaurantEntity)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<RestaurantEntity>)

    @RestResource(exported = false)
    override fun <S : RestaurantEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : RestaurantEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()
}
