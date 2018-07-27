package com.drestaurant.configuration

import com.drestaurant.query.model.*
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class RestConfiguration : RepositoryRestConfigurerAdapter() {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(CustomerEntity::class.java, CourierEntity::class.java, RestaurantEntity::class.java, OrderEntity::class.java, RestaurantOrderEntity::class.java)
    }
}