package com.drestaurant.query.repository

import com.drestaurant.query.model.RestaurantEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface RestaurantRepository : PagingAndSortingRepository<RestaurantEntity, String>
