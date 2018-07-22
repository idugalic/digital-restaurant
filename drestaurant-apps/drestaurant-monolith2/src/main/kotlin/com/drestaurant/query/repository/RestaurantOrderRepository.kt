package com.drestaurant.query.repository

import com.drestaurant.query.model.RestaurantOrderEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "orders", path = "restaurant-orders")
interface RestaurantOrderRepository : PagingAndSortingRepository<RestaurantOrderEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: RestaurantOrderEntity)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<RestaurantOrderEntity>)

    @RestResource(exported = false)
    override fun <S : RestaurantOrderEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : RestaurantOrderEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()

}
