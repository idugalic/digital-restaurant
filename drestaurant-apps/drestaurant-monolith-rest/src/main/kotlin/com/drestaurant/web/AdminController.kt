package com.drestaurant.web

import com.drestaurant.admin.AxonAdministration
import org.axonframework.eventhandling.EventProcessor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class AdminController(private val axonAdministration: AxonAdministration) {

    @RequestMapping(value = "/administration/eventprocessors", method = [RequestMethod.GET])
    fun getEventProcessors(): List<EventProcessor> {
        return axonAdministration.getTrackingEventProcessors()
    }

    @RequestMapping(value = "/administration/eventprocessors/{groupName}", method = [RequestMethod.GET])
    fun getEventProcessor(@PathVariable groupName: String): Optional<EventProcessor> {
        return axonAdministration.getEventProcessor(groupName)
    }

    @RequestMapping(value = "/administration/eventprocessors/{groupName}/reply", method = [RequestMethod.POST])
    fun replyEventProcessor(@PathVariable groupName: String): ResponseEntity<Any> {
        axonAdministration.resetTrackingEventProcessor(groupName)
        return ResponseEntity.accepted().build()
    }

}