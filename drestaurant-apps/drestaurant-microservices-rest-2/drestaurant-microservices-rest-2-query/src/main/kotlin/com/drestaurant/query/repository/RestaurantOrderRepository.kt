package com.drestaurant.query.repository

import com.drestaurant.query.model.RestaurantOrderEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface RestaurantOrderRepository : PagingAndSortingRepository<RestaurantOrderEntity, String>
