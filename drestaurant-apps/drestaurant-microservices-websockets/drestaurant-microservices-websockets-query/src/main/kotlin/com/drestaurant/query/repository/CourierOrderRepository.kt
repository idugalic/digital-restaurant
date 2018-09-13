package com.drestaurant.query.repository

import com.drestaurant.query.model.CourierOrderEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface CourierOrderRepository : PagingAndSortingRepository<CourierOrderEntity, String>
