package com.drestaurant.web

import com.drestaurant.admin.AxonAdministration
import org.axonframework.eventhandling.EventProcessor
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.*


@Controller
class AdminController(private val axonAdministration: AxonAdministration) {

    @SubscribeMapping("/eventprocessors")
    fun getEventProcessors(): Iterable<EventProcessor> {
        return axonAdministration.getTrackingEventProcessors()
    }

    @SubscribeMapping("/eventprocessors/{groupName}")
    fun getEventProcessor(@DestinationVariable groupName: String): Optional<EventProcessor> {
        return axonAdministration.getEventProcessor(groupName)
    }

    @MessageMapping(value = "/eventprocessors/{groupName}/reply")
    fun replyEventProcessor(@DestinationVariable groupName: String): ResponseEntity<Any> {
        axonAdministration.resetTrackingEventProcessor(groupName)
        return ResponseEntity.accepted().build()
    }

}