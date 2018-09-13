package com.drestaurant.query.repository

import com.drestaurant.query.model.CourierEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface CourierRepository : PagingAndSortingRepository<CourierEntity, String>