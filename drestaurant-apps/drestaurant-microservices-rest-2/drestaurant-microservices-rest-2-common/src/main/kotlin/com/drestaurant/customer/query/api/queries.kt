package com.drestaurant.customer.query.api

import com.drestaurant.customer.domain.api.model.CustomerId

data class FindCustomerQuery(val customerId: CustomerId)
