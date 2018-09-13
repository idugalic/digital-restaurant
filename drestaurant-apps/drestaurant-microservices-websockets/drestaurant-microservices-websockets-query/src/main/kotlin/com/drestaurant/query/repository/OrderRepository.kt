package com.drestaurant.query.repository

import com.drestaurant.query.model.OrderEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface OrderRepository : PagingAndSortingRepository<OrderEntity, String>