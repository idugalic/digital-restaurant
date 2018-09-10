package com.drestaurant.query.repository

import com.drestaurant.query.model.OrderEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
interface OrderRepository : PagingAndSortingRepository<OrderEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: OrderEntity)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<OrderEntity>)

    @RestResource(exported = false)
    override fun <S : OrderEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : OrderEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()

}
