package com.drestaurant.configuration

import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.model.CustomerEntity
import com.drestaurant.query.model.OrderEntity
import com.drestaurant.query.model.RestaurantEntity
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class RestConfiguration : RepositoryRestConfigurerAdapter() {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(CustomerEntity::class.java, CourierEntity::class.java, RestaurantEntity::class.java, OrderEntity::class.java)
    }
}