package com.drestaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class MicroservicesDiscoveryServerApplication

fun main(args: Array<String>) {
    runApplication<MicroservicesDiscoveryServerApplication>(*args)
}
