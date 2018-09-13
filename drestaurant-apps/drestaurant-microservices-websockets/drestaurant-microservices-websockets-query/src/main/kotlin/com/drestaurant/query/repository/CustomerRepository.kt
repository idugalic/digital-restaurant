package com.drestaurant.query.repository

import com.drestaurant.query.model.CustomerEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface CustomerRepository : PagingAndSortingRepository<CustomerEntity, String>
