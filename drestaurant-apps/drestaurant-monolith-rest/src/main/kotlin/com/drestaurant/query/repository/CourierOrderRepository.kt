package com.drestaurant.query.repository

import com.drestaurant.query.model.CourierOrderEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(collectionResourceRel = "orders", path = "courier-orders")
interface CourierOrderRepository : PagingAndSortingRepository<CourierOrderEntity, String> {

    @RestResource(exported = false)
    override fun deleteById(aLong: String)

    @RestResource(exported = false)
    override fun delete(entity: CourierOrderEntity)

    @RestResource(exported = false)
    override fun deleteAll(entities: MutableIterable<CourierOrderEntity>)

    @RestResource(exported = false)
    override fun <S : CourierOrderEntity?> save(entity: S): S

    @RestResource(exported = false)
    override fun <S : CourierOrderEntity?> saveAll(entities: MutableIterable<S>): MutableIterable<S>

    @RestResource(exported = false)
    override fun deleteAll()

}
