package com.drestaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class MicroservicesWebsocketsRestaurantApplication

fun main(args: Array<String>) {
    runApplication<MicroservicesWebsocketsRestaurantApplication>(*args)
}

